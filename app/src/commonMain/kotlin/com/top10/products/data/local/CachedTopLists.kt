package com.top10.products.data.local

import com.top10.products.domain.model.Offer
import com.top10.products.domain.model.ProductCategory
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.TopListPublisher
import com.top10.products.domain.repository.TopOffersRepository
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The top lists, read from [remote] once and kept in [dao], so opening a store again does not read
 * Firestore again. A cached list is kept until the day after the first of its deals ends ("valid
 * until 07/10" keeps it through 7 October in [timeZone]); then it is deleted and read again. A list
 * whose deals give no end is read again after [maxAge]. A store with no list published is not
 * cached at all, so it is read from Firestore every time until one is. When
 * [remote] fails, a copy that has not ended yet is shown rather than an error. Publishing writes
 * through, so the new list is cached at once.
 */
@OptIn(ExperimentalTime::class)
class CachedTopLists(
    private val remote: TopOffersRepository,
    private val publisher: TopListPublisher,
    private val dao: TopListDao,
    private val stores: StoreRepository,
    /** The time now, in milliseconds since the epoch. */
    private val now: () -> Long,
    private val timeZone: TimeZone = TimeZone.of("Europe/Brussels"),
    private val maxAge: Duration = MAX_AGE,
) : TopOffersRepository, TopListPublisher {

    override suspend fun topOffers(storeId: String): TopOffers {
        val store = stores.stores().firstOrNull { it.id == storeId } ?: error("Unknown store $storeId")
        var cached = dao.list(storeId)
        if (cached != null) {
            val expiresOn = cached.expiresOnEpochDay
            if (expiresOn != null && today().toEpochDays() >= expiresOn) {
                // Its deals are over: drop it so they are never shown again, and read the new list.
                dao.delete(storeId)
                cached = null
            } else if (expiresOn != null || now() - cached.fetchedAtMillis in 0 until maxAge.inWholeMilliseconds) {
                return TopOffers(store, cachedOffers(storeId))
            }
        }
        val top = try {
            remote.topOffers(storeId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached == null) throw e
            return TopOffers(store, cachedOffers(storeId))
        }
        save(top)
        return top
    }

    override suspend fun publish(top: TopOffers) {
        publisher.publish(top)
        save(top)
    }

    private suspend fun save(top: TopOffers) {
        if (top.offers.isEmpty()) {
            // Nothing published yet: keep nothing, so the next open asks Firestore again.
            dao.delete(top.store.id)
            return
        }
        val today = today()
        // The day after the first deal ends; deals whose end cannot be read do not count.
        val expiresOn = top.offers
            .mapNotNull { offer -> offer.deal.validUntil?.let { dueDate(it, fetchedOn = today) } }
            .minOrNull()
            ?.plus(1, DateTimeUnit.DAY)
        val list = CachedTopListEntity(top.store.id, now(), expiresOn?.toEpochDays())
        dao.replace(list, top.offers.map { it.toEntity(top.store.id) })
    }

    private fun today(): LocalDate = Instant.fromEpochMilliseconds(now()).toLocalDateTime(timeZone).date

    private suspend fun cachedOffers(storeId: String): List<Offer> = dao.offers(storeId).map { it.toOffer() }

    private fun Offer.toEntity(storeId: String) = CachedOfferEntity(
        storeId = storeId,
        rank = rank,
        name = deal.name,
        brand = deal.brand,
        packageSize = deal.packageSize,
        label = deal.label,
        price = deal.price,
        priceUnit = deal.priceUnit,
        regularPrice = deal.regularPrice,
        needsLoyaltyCard = deal.needsLoyaltyCard,
        category = deal.category.name,
        validUntil = deal.validUntil,
    )

    private fun CachedOfferEntity.toOffer() = Offer(
        rank = rank,
        deal = WeeklyDeal(
            id = "$storeId-top-$rank",
            name = name,
            brand = brand,
            packageSize = packageSize,
            label = label,
            price = price,
            priceUnit = priceUnit,
            regularPrice = regularPrice,
            needsLoyaltyCard = needsLoyaltyCard,
            category = ProductCategory.fromName(category),
            validUntil = validUntil,
        ),
    )

    companion object {
        /** How long a list whose deals give no end is cached before Firestore is read again. */
        val MAX_AGE: Duration = 12.hours
    }
}

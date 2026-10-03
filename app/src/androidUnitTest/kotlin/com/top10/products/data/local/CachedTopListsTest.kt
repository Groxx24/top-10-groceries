package com.top10.products.data.local

import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.domain.model.LocalizedText
import com.top10.products.domain.model.Offer
import com.top10.products.domain.model.ProductCategory
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.repository.TopListPublisher
import com.top10.products.domain.repository.TopOffersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class CachedTopListsTest {

    private val catalog = HardcodedCatalog()
    private val remote = FakeFirestore()
    private val dao = FakeTopListDao()
    // Noon on Thursday 1 October 2026 in Brussels.
    private var now = 1_790_848_800_000L
    private val topLists = CachedTopLists(remote, remote, dao, catalog, now = { now }, maxAge = 12.hours)

    @Test
    fun `a list is served from the cache until its first deal has ended`() = runTest {
        val store = catalog.stores().first()
        remote.lists[store.id] = listOf(offer(1, "Bananas", until = "07/10"), offer(2, "Butter", until = "14/10"))

        val first = topLists.topOffers(store.id)
        now += 6.days.inWholeMilliseconds // Wednesday 7 October, the last day of the bananas.
        val second = topLists.topOffers(store.id)

        assertEquals(1, remote.reads)
        assertEquals(first, second)
        assertEquals(ProductCategory.FRUIT, second.offers.first().deal.category)

        now += 1.days.inWholeMilliseconds // Thursday 8 October: the list is over.
        remote.lists[store.id] = listOf(offer(1, "Apples", until = "14/10"))
        assertEquals(listOf("Apples"), topLists.topOffers(store.id).offers.map { it.deal.name.en })
        assertEquals(2, remote.reads)
    }

    @Test
    fun `an ended list is deleted, not shown, when Firestore fails`() = runTest {
        val store = catalog.stores().first()
        remote.lists[store.id] = listOf(offer(1, "Bananas", until = "07/10"))
        topLists.topOffers(store.id)

        now += 7.days.inWholeMilliseconds
        remote.failing = true

        assertTrue(runCatching { topLists.topOffers(store.id) }.isFailure)
        assertEquals(null, dao.list(store.id))
        assertTrue(dao.offers(store.id).isEmpty())
    }

    @Test
    fun `a list that gives no end is read again after the max age`() = runTest {
        val store = catalog.stores().first()
        remote.lists[store.id] = listOf(offer(1, "Bananas", until = null))

        topLists.topOffers(store.id)
        now += 11.hours.inWholeMilliseconds
        topLists.topOffers(store.id)
        assertEquals(1, remote.reads)

        now += 1.hours.inWholeMilliseconds
        topLists.topOffers(store.id)
        assertEquals(2, remote.reads)
    }

    @Test
    fun `a store with no list is read from Firestore until one is published`() = runTest {
        val store = catalog.stores().first()

        assertTrue(topLists.topOffers(store.id).offers.isEmpty())
        remote.lists[store.id] = listOf(offer(1, "Bananas"))
        val published = topLists.topOffers(store.id)

        assertEquals(2, remote.reads)
        assertEquals(listOf("Bananas"), published.offers.map { it.deal.name.en })
    }

    @Test
    fun `each store is cached on its own`() = runTest {
        val (delhaize, aldi) = catalog.stores()
        remote.lists[delhaize.id] = listOf(offer(1, "Bananas"))
        remote.lists[aldi.id] = listOf(offer(1, "Avocados"))

        topLists.topOffers(delhaize.id)
        val aldiTop = topLists.topOffers(aldi.id)
        topLists.topOffers(delhaize.id)
        topLists.topOffers(aldi.id)

        assertEquals(2, remote.reads)
        assertEquals(aldi, aldiTop.store)
        assertEquals(listOf("Avocados"), aldiTop.offers.map { it.deal.name.en })
    }

    @Test
    fun `an old list that has not ended is shown when Firestore fails`() = runTest {
        val store = catalog.stores().first()
        remote.lists[store.id] = listOf(offer(1, "Bananas", until = null))
        topLists.topOffers(store.id)

        now += 24.hours.inWholeMilliseconds
        remote.failing = true

        assertEquals(listOf("Bananas"), topLists.topOffers(store.id).offers.map { it.deal.name.en })
    }

    @Test
    fun `Firestore failing with nothing cached fails`() = runTest {
        remote.failing = true
        assertTrue(runCatching { topLists.topOffers(catalog.stores().first().id) }.isFailure)
    }

    @Test
    fun `a published list is cached without reading it back`() = runTest {
        val store = catalog.stores().first()
        val top = TopOffers(store, listOf(offer(1, "Bananas")))

        topLists.publish(top)

        assertEquals(top.offers, remote.published.single().offers)
        assertEquals(top.offers.map { it.deal.name }, topLists.topOffers(store.id).offers.map { it.deal.name })
        assertEquals(0, remote.reads)
    }

    // Named the way Firestore and the cache name a published offer, so read-backs compare equal.
    private fun offer(rank: Int, name: String, until: String? = "07/10") = Offer(
        rank = rank,
        deal = WeeklyDeal(
            id = "delhaize-top-$rank", name = LocalizedText.same(name), brand = null, packageSize = null, label = null,
            price = 1.49, category = ProductCategory.FRUIT, validUntil = until,
        ),
    )

    private inner class FakeFirestore : TopOffersRepository, TopListPublisher {
        val lists = mutableMapOf<String, List<Offer>>()
        val published = mutableListOf<TopOffers>()
        var reads = 0
        var failing = false

        override suspend fun topOffers(storeId: String): TopOffers {
            reads++
            if (failing) error("offline")
            return TopOffers(catalog.stores().first { it.id == storeId }, lists[storeId].orEmpty())
        }

        override suspend fun publish(top: TopOffers) {
            published += top
        }
    }
}

/** Does in memory what the queries of [TopListDao] do in SQL. */
private class FakeTopListDao : TopListDao {
    private val lists = mutableMapOf<String, CachedTopListEntity>()
    private val offers = mutableListOf<CachedOfferEntity>()

    override suspend fun list(storeId: String): CachedTopListEntity? = lists[storeId]

    override suspend fun offers(storeId: String) = offers.filter { it.storeId == storeId }.sortedBy { it.rank }

    override suspend fun deleteOffers(storeId: String) {
        offers.removeAll { it.storeId == storeId }
    }

    override suspend fun insertOffers(offers: List<CachedOfferEntity>) {
        this.offers += offers
    }

    override suspend fun upsertList(list: CachedTopListEntity) {
        lists[list.storeId] = list
    }

    override suspend fun deleteList(storeId: String) {
        lists.remove(storeId)
    }
}

package com.top10.deals.data.firebase

import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.Offer
import com.top10.deals.domain.model.ProductCategory
import com.top10.deals.domain.model.TopOffers
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.repository.StoreRepository
import com.top10.deals.domain.repository.store
import com.top10.deals.domain.repository.TopListPublisher
import com.top10.deals.domain.repository.TopOffersRepository
import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

/**
 * The stores' top lists in Firestore, one document per store at `topLists/{storeId}`, replaced on
 * every submit. Each entry holds what the pick screen shows for a deal, nothing more, with the
 * name, pack size and label in English, French and Dutch. The store
 * itself comes from [stores], so a store without a document has an empty list.
 */
class FirestoreTopLists(
    private val firestore: FirebaseFirestore,
    private val stores: StoreRepository,
) : TopOffersRepository, TopListPublisher {

    override suspend fun topOffers(storeId: String): TopOffers {
        val store = stores.store(storeId)
        val snapshot = document(storeId).get()
        if (!snapshot.exists) return TopOffers(store, emptyList())
        val offers = snapshot.data<TopListDocument>().offers
            .sortedBy { it.rank }
            .map { it.toOffer(storeId) }
        return TopOffers(store, offers)
    }

    override suspend fun publish(top: TopOffers) {
        val document = TopListDocument(
            storeId = top.store.id,
            storeName = top.store.name,
            offers = top.offers.map { it.toEntry() },
        )
        // encodeDefaults writes submittedAt, which only has a default value.
        document(top.store.id).set(document, encodeDefaults = true)
    }

    private fun document(storeId: String) = firestore.collection(COLLECTION).document(storeId)

    private fun Offer.toEntry() = TopListEntry(
        rank = rank,
        name = deal.name.toEntry(),
        brand = deal.brand,
        packageSize = deal.packageSize?.toEntry(),
        label = deal.label?.toEntry(),
        price = deal.price,
        priceUnit = deal.priceUnit,
        regularPrice = deal.regularPrice,
        needsLoyaltyCard = deal.needsLoyaltyCard,
        category = deal.category.name,
        validUntil = deal.validUntil,
    )

    private fun TopListEntry.toOffer(storeId: String) = Offer(
        rank = rank,
        deal = WeeklyDeal(
            id = "$storeId-top-$rank",
            name = name.toText(),
            brand = brand,
            packageSize = packageSize?.toText(),
            label = label?.toText(),
            price = price,
            priceUnit = priceUnit,
            regularPrice = regularPrice,
            needsLoyaltyCard = needsLoyaltyCard,
            category = ProductCategory.fromName(category),
            validUntil = validUntil,
        ),
    )

    private fun LocalizedText.toEntry() = TextEntry(en = en, fr = fr, nl = nl)

    private fun TextEntry.toText() = LocalizedText(en = en, fr = fr, nl = nl)

    private companion object {
        const val COLLECTION = "topLists"
    }
}

@Serializable
private data class TopListDocument(
    val storeId: String,
    val storeName: String,
    /** Set by the server when the document is written. */
    val submittedAt: BaseTimestamp = Timestamp.ServerTimestamp,
    /** Best first; `rank` 1 to 10. */
    val offers: List<TopListEntry>,
)

@Serializable
private data class TopListEntry(
    val rank: Int,
    val name: TextEntry,
    val brand: String? = null,
    val packageSize: TextEntry? = null,
    val label: TextEntry? = null,
    val price: Double? = null,
    val priceUnit: String? = null,
    val regularPrice: Double? = null,
    val needsLoyaltyCard: Boolean = false,
    /** A [ProductCategory] name; missing in lists published before categories, which show as OTHER. */
    val category: String? = null,
    val validUntil: String? = null,
)

/** A text in every language the app has, so each phone shows its own. */
@Serializable
private data class TextEntry(
    val en: String,
    val fr: String,
    val nl: String,
)

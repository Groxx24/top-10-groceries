package com.top10.products.domain.model

/**
 * A promotion in a store's folder this week, as the folder prints it. The 20 most relevant are
 * the candidates someone picks a store's top 10 from, and each place in a published top list
 * ([Offer]) is one of them.
 */
data class WeeklyDeal(
    /** Unique within one store's deals. */
    val id: String,
    val name: LocalizedText,
    val brand: String?,
    /** Pack size as the store prints it ("500 g", "6 x 25 cl"), if known. */
    val packageSize: LocalizedText?,
    /** The deal in the store's own words ("1+1 gratis", "-31%"), shown as is; null when the folder gives only prices. */
    val label: LocalizedText?,
    /** What the deal costs, in euros, if the folder gives it. */
    val price: Double?,
    /** What [price] is for, as the folder prints it ("/2", "/kg", "/promopack"); null for one item. */
    val priceUnit: String? = null,
    /** The price before the deal, in euros, if the folder prints it; gives the percentage off when there is no [label]. */
    val regularPrice: Double? = null,
    /** True when the deal only applies with the store's loyalty card or app. */
    val needsLoyaltyCard: Boolean = false,
    /** What kind of product it is; the app shows it as a picture. */
    val category: ProductCategory = ProductCategory.OTHER,
    /** Last day of the deal as the store prints it ("07/10"), if known. */
    val validUntil: String?,
)

/** Share of [WeeklyDeal.regularPrice] saved, from 0 to 1, when the folder gives both prices. */
val WeeklyDeal.discount: Double?
    get() {
        val regular = regularPrice ?: return null
        val now = price ?: return null
        return if (regular > 0 && now <= regular) 1 - now / regular else null
    }

/** A store's candidates for its top list, most relevant first. */
data class WeeklyDeals(
    val store: Store,
    val deals: List<WeeklyDeal>,
)

/** How many products a store's published top list holds; a submission must pick exactly this many. */
const val TOP_LIST_SIZE = 10

/** How many of a store's weekly deals are offered as candidates for its top list. */
const val CANDIDATE_COUNT = 20

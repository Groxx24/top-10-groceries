package com.top10.groceries.domain.model

/** A product on promotion this week, with its deal already reduced to plain numbers. */
data class Offer(
    val store: Store,
    val productCode: String,
    val brand: String?,
    val name: String,
    val category: ProductCategory,
    /** The store's own name for the category, in the language the offers were fetched in. */
    val categoryLabel: String,
    /** Normal shelf price of one item, before the deal. */
    val price: Double,
    /** Pack size as the store prints it ("500 gr", "6 x 25 cl"), if known. */
    val packageSize: String?,
    val imageUrl: String?,
    val productUrl: String?,
    val deal: Deal,
)

/**
 * What a promotion is worth. Every promotion type the store runs ("1+1 free", "2nd at -50%",
 * "3 for €5", "-€2") becomes the same two numbers, so offers can be compared with each other.
 */
data class Deal(
    /** Id of the promotion. Many products often share one ("1+1 on all Delhaize charcuterie"). */
    val promotionId: String,
    /** The store's wording, shown to the user as is. */
    val label: String,
    /** Share of the normal price saved when buying [requiredQuantity] items, from 0 to 1. */
    val discount: Double,
    /** How many items have to be bought to get [discount]. */
    val requiredQuantity: Int,
    /** True when the deal only applies with the store's loyalty card. */
    val needsLoyaltyCard: Boolean,
    /** Last day of the deal as the store prints it ("07/10"), if known. */
    val validUntil: String?,
) {
    init {
        require(discount in 0.0..1.0) { "discount must be a fraction, was $discount" }
        require(requiredQuantity >= 1) { "requiredQuantity must be at least 1, was $requiredQuantity" }
    }
}

/** Money saved by taking the deal once, in euros. */
val Offer.saving: Double get() = price * deal.requiredQuantity * deal.discount

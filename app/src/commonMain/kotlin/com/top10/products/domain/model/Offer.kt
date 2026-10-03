package com.top10.products.domain.model

/**
 * One place in a store's top list, as it is published. The list is ranked before it reaches
 * the app, so nothing here is computed on the device.
 */
data class Offer(
    /** 1 for the best offer of the week. */
    val rank: Int,
    val productId: String,
    val brand: String?,
    val name: String,
    /** The aisle as the store names it ("Meat", "Fruit & vegetables"). */
    val category: String?,
    /** Pack size as the store prints it ("500 g", "6 x 25 cl"), if known. */
    val packageSize: String?,
    val imageUrl: String?,
    val productUrl: String?,
    /** Normal shelf price of one item, before the deal. */
    val price: Double,
    val deal: Deal,
)

/** What a promotion is worth. Every store's wording ("1+1 free", "-30%") comes with the same numbers. */
data class Deal(
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

/** A store's top list for the week. */
data class TopOffers(
    val store: Store,
    /** Best first. */
    val offers: List<Offer>,
)

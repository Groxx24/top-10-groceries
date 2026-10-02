package com.top10.groceries.domain.model

/** One place in the top list. */
data class RankedOffer(
    val rank: Int,
    val offer: Offer,
    val score: OfferScore,
    /** Other products covered by the same promotion, which the list leaves out to stay varied. */
    val sameDealCount: Int,
)

/**
 * Why an offer scored what it did. [value] is the product of the four factors times 100, so a
 * half-price staple bought on its own lands near 100.
 */
data class OfferScore(
    val value: Double,
    /** How interesting the discount is on its own: 0 at the floor, 1 at half price. */
    val discountFactor: Double,
    /** How much the category matters for a weekly shop, from 0 to 1. */
    val categoryFactor: Double,
    /** Penalty for having to buy several items, 1 when one is enough. */
    val quantityFactor: Double,
    /** Small nudge for deals that save more money in absolute terms. */
    val savingFactor: Double,
)

data class TopOffers(
    val store: Store,
    val offers: List<RankedOffer>,
    /** How many products the store has on promotion this week, before ranking. */
    val consideredCount: Int,
)

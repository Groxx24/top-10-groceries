package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.OfferScore
import com.top10.groceries.domain.model.saving

/** Turns one offer into a score by [RankingPolicy]. Knows nothing about other offers. */
class OfferScorer(private val policy: RankingPolicy = RankingPolicy()) {

    /** The score of [offer], or null when its discount is too small to be of interest. */
    fun score(offer: Offer): OfferScore? {
        val discountFactor = discountFactor(offer.deal.discount)
        if (discountFactor <= 0.0) return null

        val categoryFactor = policy.categoryWeights[offer.category] ?: policy.fallbackCategoryWeight
        val quantityFactor = 1.0 / (1.0 + policy.penaltyPerExtraItem * (offer.deal.requiredQuantity - 1))
        val savingFactor = (1.0 - policy.savingShare) +
            policy.savingShare * (offer.saving / policy.savingTarget).coerceIn(0.0, 1.0)

        return OfferScore(
            value = 100.0 * discountFactor * categoryFactor * quantityFactor * savingFactor,
            discountFactor = discountFactor,
            categoryFactor = categoryFactor,
            quantityFactor = quantityFactor,
            savingFactor = savingFactor,
        )
    }

    /** A straight line from 0 at the floor through 1 at the target, capped a little above. */
    private fun discountFactor(discount: Double): Double =
        ((discount - policy.discountFloor) / (policy.discountTarget - policy.discountFloor))
            .coerceIn(0.0, policy.discountFactorCap)
}

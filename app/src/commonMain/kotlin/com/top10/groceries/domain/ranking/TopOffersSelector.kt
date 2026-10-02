package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.OfferScore
import com.top10.groceries.domain.model.ProductCategory
import com.top10.groceries.domain.model.RankedOffer

/**
 * Picks the top list out of all scored offers, keeping it varied:
 * a promotion covering many products takes one place, shown by its best product, and no
 * category takes more than [RankingPolicy.maxPerCategory] places.
 */
class TopOffersSelector(
    private val scorer: OfferScorer,
    private val policy: RankingPolicy = RankingPolicy(),
) {

    fun select(offers: List<Offer>): List<RankedOffer> {
        val bestPerPromotion = offers
            .mapNotNull { offer -> scorer.score(offer)?.let { Scored(offer, it) } }
            .groupBy { it.offer.deal.promotionId }
            .values
            .map { group -> Candidate(best = group.maxWith(BestFirst), sameDealCount = group.size - 1) }
            .sortedWith(compareBy(BestFirst.reversed()) { it.best })

        val takenPerCategory = mutableMapOf<ProductCategory, Int>()
        val picked = mutableListOf<RankedOffer>()
        for (candidate in bestPerPromotion) {
            if (picked.size == policy.listSize) break
            val category = candidate.best.offer.category
            val taken = takenPerCategory[category] ?: 0
            if (taken >= policy.maxPerCategory) continue
            takenPerCategory[category] = taken + 1
            picked += RankedOffer(
                rank = picked.size + 1,
                offer = candidate.best.offer,
                score = candidate.best.score,
                sameDealCount = candidate.sameDealCount,
            )
        }
        return picked
    }

    private class Scored(val offer: Offer, val score: OfferScore)

    private class Candidate(val best: Scored, val sameDealCount: Int)

    private companion object {
        /** Higher score wins; the product code breaks ties so the order is the same every run. */
        val BestFirst: Comparator<Scored> =
            compareBy<Scored> { it.score.value }.thenByDescending { it.offer.productCode }
    }
}

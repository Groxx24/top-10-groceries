package com.top10.products.domain.usecase

import com.top10.products.domain.model.Offer
import com.top10.products.domain.model.Store
import com.top10.products.domain.model.TOP_LIST_SIZE
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.repository.TopListPublisher

/** Publishes a store's top list. It must hold exactly [TOP_LIST_SIZE] deals, best first. */
class SubmitTopListUseCase(private val publisher: TopListPublisher) {
    suspend operator fun invoke(store: Store, deals: List<WeeklyDeal>) {
        require(deals.size == TOP_LIST_SIZE) { "A top list has $TOP_LIST_SIZE deals, got ${deals.size}" }
        require(deals.distinctBy { it.id }.size == deals.size) { "A top list cannot hold a deal twice" }
        val offers = deals.mapIndexed { index, deal -> Offer(rank = index + 1, deal = deal) }
        publisher.publish(TopOffers(store, offers))
    }
}

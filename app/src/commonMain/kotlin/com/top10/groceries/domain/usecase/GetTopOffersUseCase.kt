package com.top10.groceries.domain.usecase

import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.model.TopOffers
import com.top10.groceries.domain.ranking.TopOffersSelector
import com.top10.groceries.domain.repository.OfferRepository

/** The week's best offers at one store. */
class GetTopOffersUseCase(
    private val repository: OfferRepository,
    private val selector: TopOffersSelector,
) {
    suspend operator fun invoke(store: Store, refresh: Boolean = false): TopOffers {
        val offers = repository.currentOffers(store, refresh)
        return TopOffers(
            store = store,
            offers = selector.select(offers),
            consideredCount = offers.size,
        )
    }
}

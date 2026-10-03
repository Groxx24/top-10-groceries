package com.top10.groceries.domain.usecase

import com.top10.groceries.domain.model.TopOffers
import com.top10.groceries.domain.repository.TopOffersRepository

/** The week's best offers at one store. */
class GetTopOffersUseCase(private val repository: TopOffersRepository) {
    suspend operator fun invoke(storeId: String): TopOffers = repository.topOffers(storeId)
}

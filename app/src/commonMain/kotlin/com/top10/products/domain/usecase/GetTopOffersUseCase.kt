package com.top10.products.domain.usecase

import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.repository.TopOffersRepository

/** The week's best offers at one store. */
class GetTopOffersUseCase(private val repository: TopOffersRepository) {
    suspend operator fun invoke(storeId: String): TopOffers = repository.topOffers(storeId)
}

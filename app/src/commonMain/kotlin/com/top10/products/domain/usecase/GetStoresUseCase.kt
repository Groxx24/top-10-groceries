package com.top10.products.domain.usecase

import com.top10.products.domain.model.Store
import com.top10.products.domain.repository.StoreRepository

/** The stores the user can pick from. */
class GetStoresUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(): List<Store> = repository.stores()
}

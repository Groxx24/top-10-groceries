package com.top10.groceries.domain.usecase

import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.repository.StoreRepository

/** The stores the user can pick from. */
class GetStoresUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(): List<Store> = repository.stores()
}

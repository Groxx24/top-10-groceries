package com.top10.groceries.di

import com.top10.groceries.data.hardcoded.HardcodedCatalog
import com.top10.groceries.domain.repository.StoreRepository
import com.top10.groceries.domain.repository.TopOffersRepository
import com.top10.groceries.domain.usecase.GetStoresUseCase
import com.top10.groceries.domain.usecase.GetTopOffersUseCase

/** The one place dependencies are wired. Each platform creates one. */
class AppContainer {

    // Stand-in until the lists are read from Firebase.
    private val catalog = HardcodedCatalog()
    private val storeRepository: StoreRepository = catalog
    private val topOffersRepository: TopOffersRepository = catalog

    val getStores = GetStoresUseCase(storeRepository)
    val getTopOffers = GetTopOffersUseCase(topOffersRepository)
}

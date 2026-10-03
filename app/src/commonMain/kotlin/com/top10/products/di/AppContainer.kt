package com.top10.products.di

import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.data.hardcoded.HardcodedWeeklyDeals
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.TopOffersRepository
import com.top10.products.domain.repository.WeeklyDealsRepository
import com.top10.products.domain.usecase.GetStoresUseCase
import com.top10.products.domain.usecase.GetTopOffersUseCase
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase

/** The one place dependencies are wired. Each platform creates one. */
class AppContainer {

    // Stand-in until the lists are read from Firebase.
    private val catalog = HardcodedCatalog()
    private val storeRepository: StoreRepository = catalog
    private val topOffersRepository: TopOffersRepository = catalog

    // The candidates the debug screen picks a top list from; typed in by hand until scraped.
    private val weeklyDealsRepository: WeeklyDealsRepository = HardcodedWeeklyDeals()

    val getStores = GetStoresUseCase(storeRepository)
    val getTopOffers = GetTopOffersUseCase(topOffersRepository)
    val getWeeklyDeals = GetWeeklyDealsUseCase(storeRepository, weeklyDealsRepository)
}

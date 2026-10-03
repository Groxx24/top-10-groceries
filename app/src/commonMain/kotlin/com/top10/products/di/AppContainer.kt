package com.top10.products.di

import com.top10.products.data.firebase.FirestoreTopLists
import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.data.hardcoded.HardcodedWeeklyDeals
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.WeeklyDealsRepository
import com.top10.products.domain.usecase.GetStoresUseCase
import com.top10.products.domain.usecase.GetTopOffersUseCase
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase
import com.top10.products.domain.usecase.SubmitTopListUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

/** The one place dependencies are wired. Each platform creates one. */
class AppContainer {

    private val storeRepository: StoreRepository = HardcodedCatalog()

    // Published top lists, read by the app and written by the debug screen. Lazy so Firestore
    // starts only when a top list is opened or submitted.
    private val topLists by lazy { FirestoreTopLists(Firebase.firestore, storeRepository) }

    // The candidates the debug screen picks a top list from; typed in by hand until scraped.
    private val weeklyDealsRepository: WeeklyDealsRepository = HardcodedWeeklyDeals()

    val getStores = GetStoresUseCase(storeRepository)
    val getTopOffers by lazy { GetTopOffersUseCase(topLists) }
    val getWeeklyDeals = GetWeeklyDealsUseCase(storeRepository, weeklyDealsRepository)
    val submitTopList by lazy { SubmitTopListUseCase(topLists) }
}

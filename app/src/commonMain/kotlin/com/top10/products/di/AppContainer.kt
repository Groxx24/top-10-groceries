package com.top10.products.di

import androidx.room.RoomDatabase
import com.top10.products.data.firebase.FirestoreTopLists
import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.data.lock.HashedDebugLock
import com.top10.products.data.hardcoded.HardcodedWeeklyDeals
import com.top10.products.data.local.CachedTopLists
import com.top10.products.data.local.TopListDatabase
import com.top10.products.data.local.buildTopListDatabase
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.WeeklyDealsRepository
import com.top10.products.domain.usecase.GetStoresUseCase
import com.top10.products.domain.usecase.GetTopOffersUseCase
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase
import com.top10.products.domain.usecase.SubmitTopListUseCase
import com.top10.products.domain.usecase.UnlockDebugUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** The one place dependencies are wired. Each platform creates one, with its own database builder. */
@OptIn(ExperimentalTime::class)
class AppContainer(databaseBuilder: RoomDatabase.Builder<TopListDatabase>) {

    private val storeRepository: StoreRepository = HardcodedCatalog()

    private val database by lazy { databaseBuilder.buildTopListDatabase() }

    // Published top lists, read by the app and written by the debug screen, kept on the phone so
    // Firestore is read at most every CachedTopLists.MAX_AGE per store. Lazy so Firestore starts
    // only when a list is not cached yet or is submitted.
    private val firestoreTopLists by lazy { FirestoreTopLists(Firebase.firestore, storeRepository) }
    private val topLists by lazy {
        CachedTopLists(
            remote = firestoreTopLists,
            publisher = firestoreTopLists,
            dao = database.topListDao(),
            stores = storeRepository,
            now = { Clock.System.now().toEpochMilliseconds() },
        )
    }

    // The candidates the debug screen picks a top list from; typed in by hand until scraped.
    private val weeklyDealsRepository: WeeklyDealsRepository = HardcodedWeeklyDeals()

    val getStores = GetStoresUseCase(storeRepository)
    val getTopOffers by lazy { GetTopOffersUseCase(topLists) }
    val getWeeklyDeals = GetWeeklyDealsUseCase(storeRepository, weeklyDealsRepository)
    val submitTopList by lazy { SubmitTopListUseCase(topLists) }

    // The passphrase, hashed, that opens the debug screens on top of the debug-build check.
    val unlockDebug = UnlockDebugUseCase(HashedDebugLock())
}

package com.top10.deals.di

import androidx.room.RoomDatabase
import com.top10.deals.data.firebase.FirestoreTopLists
import com.top10.deals.data.hardcoded.HardcodedCatalog
import com.top10.deals.data.local.CachedTopLists
import com.top10.deals.data.local.TopListDatabase
import com.top10.deals.data.local.buildTopListDatabase
import com.top10.deals.domain.repository.StoreRepository
import com.top10.deals.domain.usecase.GetStoresUseCase
import com.top10.deals.domain.usecase.GetStoresWithOffersUseCase
import com.top10.deals.domain.usecase.GetTopOffersUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Where the app's dependencies are wired. Each platform creates one, with its own database
 * builder; the debug screens' extra ones are in [DebugContainer].
 */
@OptIn(ExperimentalTime::class)
class AppContainer(databaseBuilder: RoomDatabase.Builder<TopListDatabase>) {

    internal val storeRepository: StoreRepository = HardcodedCatalog()

    private val database by lazy { databaseBuilder.buildTopListDatabase() }

    // Published top lists, read by the app and written by the debug screen, kept on the phone so
    // Firestore is read at most every CachedTopLists.MAX_AGE per store. Lazy so Firestore starts
    // only when a list is not cached yet or is submitted.
    internal val firestoreTopLists by lazy { FirestoreTopLists(Firebase.firestore, storeRepository) }
    internal val topLists by lazy {
        CachedTopLists(
            remote = firestoreTopLists,
            publisher = firestoreTopLists,
            dao = database.topListDao(),
            stores = storeRepository,
            now = { Clock.System.now().toEpochMilliseconds() },
        )
    }

    val getStores = GetStoresUseCase(storeRepository)
    val getTopOffers by lazy { GetTopOffersUseCase(topLists) }
    // Reads every list at once, so the store list shows only stores with one and opening them is cached.
    val getStoresWithOffers by lazy { GetStoresWithOffersUseCase(storeRepository, topLists) }

    /** Today in Belgium, which is when the deals' "valid until" dates end. */
    internal fun today() = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Brussels")).date
}

package com.top10.deals.di

import com.top10.deals.data.firebase.FirebaseDebugLock
import com.top10.deals.data.hardcoded.HardcodedWeeklyDeals
import com.top10.deals.domain.repository.WeeklyDealsRepository
import com.top10.deals.domain.usecase.DeleteEndedTopListsUseCase
import com.top10.deals.domain.usecase.GetWeeklyDealsUseCase
import com.top10.deals.domain.usecase.SubmitTopListUseCase
import com.top10.deals.domain.usecase.UnlockDebugUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * What the debug screens need on top of [app]. Kept apart from [AppContainer] so that only a debug
 * build creates one: nothing else refers to it, so R8 drops it, the debug screens and the
 * publisher login from a release build.
 */
class DebugContainer(app: AppContainer) {

    // The candidates the debug screen picks a top list from; typed in by hand until scraped.
    private val weeklyDealsRepository: WeeklyDealsRepository = HardcodedWeeklyDeals()

    val getStores = app.getStores
    val getWeeklyDeals = GetWeeklyDealsUseCase(app.storeRepository, weeklyDealsRepository, app::today)
    val submitTopList by lazy { SubmitTopListUseCase(app.topLists) }
    val deleteEndedTopLists by lazy { DeleteEndedTopListsUseCase(app.firestoreTopLists, app::today) }

    // Signs the publisher in to Firebase, which opens the debug screens and lets them write to
    // Firestore. Lazy so Firebase Auth starts only when asked.
    val unlockDebug by lazy { UnlockDebugUseCase(FirebaseDebugLock(Firebase.auth)) }
}

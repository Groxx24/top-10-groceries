package com.top10.products.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.top10.products.di.AppContainer
import com.top10.products.ui.debug.DebugRoute
import com.top10.products.ui.debug.PickTopRoute
import com.top10.products.ui.debug.UnlockRoute
import com.top10.products.ui.stores.StoresRoute
import com.top10.products.ui.theme.Top10Theme
import com.top10.products.ui.top.TopOffersRoute

/**
 * Two screens: the store list, and the top list of the store picked there. Debug builds
 * ([isDebugBuild]) add a store picker, reached from the store list, that leads to picking that
 * store's top 10 from its weekly deals. Those screens also need the passphrase, asked for each
 * time they are opened.
 */
@Composable
fun App(container: AppContainer, isDebugBuild: Boolean) {
    Top10Theme {
        var openStoreId by rememberSaveable { mutableStateOf<String?>(null) }
        var debugOpen by rememberSaveable { mutableStateOf(false) }
        var debugStoreId by rememberSaveable { mutableStateOf<String?>(null) }
        // Set by the right passphrase, and cleared on leaving the debug screens so they lock again.
        var debugUnlocked by rememberSaveable { mutableStateOf(false) }
        val closeDebug = {
            debugOpen = false
            debugUnlocked = false
            debugStoreId = null
        }
        val storeId = openStoreId
        val pickStoreId = debugStoreId
        val debugAllowed = isDebugBuild && debugOpen && debugUnlocked
        if (isDebugBuild && debugOpen && !debugUnlocked) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                onBackCompleted = closeDebug,
            )
            UnlockRoute(container, onUnlocked = { debugUnlocked = true }, onBack = closeDebug)
        } else if (debugAllowed && pickStoreId != null) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                onBackCompleted = { debugStoreId = null },
            )
            PickTopRoute(container, pickStoreId, onBack = { debugStoreId = null })
        } else if (debugAllowed) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                onBackCompleted = closeDebug,
            )
            DebugRoute(container, onBack = closeDebug, onPickStore = { debugStoreId = it })
        } else if (storeId == null) {
            StoresRoute(
                container,
                onOpenStore = { openStoreId = it },
                onOpenDebug = if (isDebugBuild) ({ debugOpen = true }) else null,
            )
        } else {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                onBackCompleted = { openStoreId = null },
            )
            TopOffersRoute(container, storeId, onBack = { openStoreId = null })
        }
    }
}

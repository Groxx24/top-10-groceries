package com.top10.deals.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.top10.deals.di.AppContainer
import com.top10.deals.ui.debug.DebugRoute
import com.top10.deals.ui.debug.PickTopRoute
import com.top10.deals.ui.debug.UnlockRoute
import com.top10.deals.ui.stores.StoresRoute
import com.top10.deals.ui.theme.Top10Theme
import com.top10.deals.ui.top.TopOffersRoute

/**
 * Two screens: the store list, and the top list of the store picked there. Debug builds
 * ([isDebugBuild]) add a store picker, reached from the store list, that leads to picking that
 * store's top 10 from its weekly deals. Those screens also need the passphrase, asked for each
 * time they are opened.
 *
 * Ads come from the platform: [storesBanner] is drawn under the store list, and
 * [onOpenPrivacyOptions], when not null, lets the user change their consent to ads.
 */
@Composable
fun App(
    container: AppContainer,
    isDebugBuild: Boolean,
    storesBanner: @Composable () -> Unit = {},
    onOpenPrivacyOptions: (() -> Unit)? = null,
) {
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
        val closePicker = { debugStoreId = null }
        val closeStore = { openStoreId = null }
        when (val screen = currentScreen(isDebugBuild, openStoreId, debugOpen, debugUnlocked, debugStoreId)) {
            Screen.Unlock -> {
                OnBack(closeDebug)
                UnlockRoute(container, onUnlocked = { debugUnlocked = true }, onBack = closeDebug)
            }

            Screen.PickStore -> {
                OnBack(closeDebug)
                DebugRoute(container, onBack = closeDebug, onPickStore = { debugStoreId = it })
            }

            is Screen.PickTop -> {
                OnBack(closePicker)
                PickTopRoute(container, screen.storeId, onBack = closePicker)
            }

            Screen.Stores -> StoresRoute(
                container,
                onOpenStore = { openStoreId = it },
                onOpenDebug = if (isDebugBuild) ({ debugOpen = true }) else null,
                onOpenPrivacyOptions = onOpenPrivacyOptions,
                banner = storesBanner,
            )

            is Screen.Top -> {
                OnBack(closeStore)
                TopOffersRoute(container, screen.storeId, onBack = closeStore)
            }
        }
    }
}

private sealed interface Screen {
    data object Stores : Screen
    data class Top(val storeId: String) : Screen
    data object Unlock : Screen
    data object PickStore : Screen
    data class PickTop(val storeId: String) : Screen
}

/**
 * The screen the navigation state stands for. The debug screens only exist in a debug build, and
 * only once unlocked; until then opening them shows the passphrase screen.
 */
private fun currentScreen(
    isDebugBuild: Boolean,
    openStoreId: String?,
    debugOpen: Boolean,
    debugUnlocked: Boolean,
    debugStoreId: String?,
): Screen = when {
    isDebugBuild && debugOpen -> when {
        !debugUnlocked -> Screen.Unlock
        debugStoreId != null -> Screen.PickTop(debugStoreId)
        else -> Screen.PickStore
    }

    openStoreId != null -> Screen.Top(openStoreId)
    else -> Screen.Stores
}

/** Makes the system back gesture call [onBack] while this screen is shown. */
@Composable
private fun OnBack(onBack: () -> Unit) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = onBack,
    )
}

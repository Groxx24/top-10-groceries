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
import com.top10.deals.ui.stores.StoresRoute
import com.top10.deals.ui.theme.Top10Theme
import com.top10.deals.ui.top.TopOffersRoute

/**
 * Two screens: the store list, and the top list of the store picked there. A debug build passes
 * [debugScreens] (see `ui/debug/DebugScreens`), reached from the store list and left through
 * their `onClose`; a release build passes null, so nothing here refers to them.
 *
 * Ads come from the platform: [storesBanner] is drawn under the store list, [dealsBanner] under a
 * store's top list, and [onOpenPrivacyOptions], when not null, lets the user change their consent to ads.
 */
@Composable
fun App(
    container: AppContainer,
    debugScreens: (@Composable (onClose: () -> Unit) -> Unit)? = null,
    storesBanner: @Composable () -> Unit = {},
    dealsBanner: @Composable () -> Unit = {},
    onOpenPrivacyOptions: (() -> Unit)? = null,
) {
    Top10Theme {
        var openStoreId by rememberSaveable { mutableStateOf<String?>(null) }
        var debugOpen by rememberSaveable { mutableStateOf(false) }
        val closeStore = { openStoreId = null }
        val storeId = openStoreId
        when {
            debugScreens != null && debugOpen -> debugScreens { debugOpen = false }

            storeId != null -> {
                OnBack(closeStore)
                TopOffersRoute(container, storeId, onBack = closeStore, banner = dealsBanner)
            }

            else -> StoresRoute(
                container,
                onOpenStore = { openStoreId = it },
                onOpenDebug = if (debugScreens != null) ({ debugOpen = true }) else null,
                onOpenPrivacyOptions = onOpenPrivacyOptions,
                banner = storesBanner,
            )
        }
    }
}

/** Makes the system back gesture call [onBack] while this screen is shown. */
@Composable
internal fun OnBack(onBack: () -> Unit) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = onBack,
    )
}

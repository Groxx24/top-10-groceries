package com.top10.deals.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.top10.deals.di.DebugContainer
import com.top10.deals.ui.OnBack

/**
 * The debug screens: the passphrase, then a store picker, then picking that store's top 10.
 * [onClose] leaves them. Their state lives here, so leaving forgets it and they lock again.
 */
@Composable
fun DebugScreens(container: DebugContainer, onClose: () -> Unit) {
    var unlocked by rememberSaveable { mutableStateOf(false) }
    var storeId by rememberSaveable { mutableStateOf<String?>(null) }
    val pickingFor = storeId
    when {
        !unlocked -> {
            OnBack(onClose)
            UnlockRoute(container, onUnlocked = { unlocked = true }, onBack = onClose)
        }

        pickingFor != null -> {
            val closePicker = { storeId = null }
            OnBack(closePicker)
            PickTopRoute(container, pickingFor, onBack = closePicker)
        }

        else -> {
            OnBack(onClose)
            DebugRoute(container, onBack = onClose, onPickStore = { storeId = it })
        }
    }
}

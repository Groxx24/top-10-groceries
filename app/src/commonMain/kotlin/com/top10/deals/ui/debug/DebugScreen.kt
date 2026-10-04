package com.top10.deals.ui.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.deals.di.AppContainer
import com.top10.deals.resources.Res
import com.top10.deals.resources.debug_pick_store_title
import com.top10.deals.resources.debug_title
import com.top10.deals.ui.BackTopBar
import com.top10.deals.ui.stores.StoreList
import com.top10.deals.ui.stores.StoresUiState
import com.top10.deals.ui.stores.StoresViewModel
import org.jetbrains.compose.resources.stringResource

/** Tools for debug builds only; the store list shows its entry point only when the build is debuggable. */
@Composable
fun DebugRoute(container: AppContainer, onBack: () -> Unit, onPickStore: (storeId: String) -> Unit) {
    // Same key as the store list's, so both screens share one loaded list.
    val viewModel = viewModel { StoresViewModel(container.getStores) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    DebugScreen(state = state, onRetry = viewModel::onRetry, onPickStore = onPickStore, onBack = onBack)
}

@Composable
fun DebugScreen(
    state: StoresUiState,
    onRetry: () -> Unit,
    onPickStore: (storeId: String) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { BackTopBar(stringResource(Res.string.debug_title), onBack) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = stringResource(Res.string.debug_pick_store_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
            )
            HorizontalDivider()
            StoreList(state, onRetry, onPickStore, Modifier.weight(1f))
        }
    }
}

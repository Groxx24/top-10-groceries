package com.top10.products.ui.stores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.Store
import com.top10.products.resources.Res
import com.top10.products.resources.error_hint
import com.top10.products.resources.loading
import com.top10.products.resources.retry
import com.top10.products.resources.stores_error_title
import com.top10.products.resources.stores_hint
import com.top10.products.resources.stores_title
import com.top10.products.ui.Message
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoresRoute(container: AppContainer, onOpenStore: (storeId: String) -> Unit) {
    val viewModel = viewModel { StoresViewModel(container.getStores) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoresScreen(state = state, onRetry = viewModel::onRetry, onOpenStore = onOpenStore)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    state: StoresUiState,
    onRetry: () -> Unit,
    onOpenStore: (storeId: String) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.stores_title)) }) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.loadFailed -> Message(
                    title = stringResource(Res.string.stores_error_title),
                    hint = stringResource(Res.string.error_hint),
                ) {
                    Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
                }

                state.isLoading -> Message(title = stringResource(Res.string.loading)) {
                    CircularProgressIndicator()
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(Res.string.stores_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    items(state.stores, key = { it.id }) { store ->
                        StoreCard(store = store, onClick = { onOpenStore(store.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreCard(store: Store, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = store.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
        )
    }
}

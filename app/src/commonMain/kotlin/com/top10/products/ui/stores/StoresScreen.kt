package com.top10.products.ui.stores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.Store
import com.top10.products.resources.Res
import com.top10.products.resources.debug_title
import com.top10.products.resources.stores_card_hint
import com.top10.products.resources.stores_error_title
import com.top10.products.resources.stores_hint
import com.top10.products.resources.stores_section
import com.top10.products.resources.stores_title
import com.top10.products.ui.BackArrow
import com.top10.products.ui.ErrorMessage
import com.top10.products.ui.LoadingMessage
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoresRoute(
    container: AppContainer,
    onOpenStore: (storeId: String) -> Unit,
    onOpenDebug: (() -> Unit)?,
) {
    val viewModel = viewModel { StoresViewModel(container.getStores) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoresScreen(
        state = state,
        onRetry = viewModel::onRetry,
        onOpenStore = onOpenStore,
        onOpenDebug = onOpenDebug,
    )
}

@Composable
fun StoresScreen(
    state: StoresUiState,
    onRetry: () -> Unit,
    onOpenStore: (storeId: String) -> Unit,
    /** Null hides the entry point, which only debug builds show. */
    onOpenDebug: (() -> Unit)? = null,
) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Header(
                onOpenDebug = onOpenDebug,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp),
            )
            HorizontalDivider()
            StoreList(state, onRetry, onOpenStore, Modifier.weight(1f))
        }
    }
}

/** The stores with their loading and error states; the debug screen shows it too. */
@Composable
fun StoreList(
    state: StoresUiState,
    onRetry: () -> Unit,
    onOpenStore: (storeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when {
            state.loadFailed -> ErrorMessage(stringResource(Res.string.stores_error_title), onRetry)
            state.isLoading -> LoadingMessage()

            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.stores_section),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                    )
                }
                items(state.stores, key = { it.id }) { store ->
                    StoreCard(store = store, onClick = { onOpenStore(store.id) })
                }
            }
        }
    }
}

/** The app's name and what it does, above the list. */
@Composable
private fun Header(onOpenDebug: (() -> Unit)?, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.stores_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            if (onOpenDebug != null) {
                TextButton(onClick = onOpenDebug) { Text(stringResource(Res.string.debug_title)) }
            }
        }
        Text(
            text = stringResource(Res.string.stores_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StoreCard(store: Store, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StoreLogo(store)
            Column(Modifier.weight(1f)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(Res.string.stores_card_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // The back arrow turned around, so the app needs no icons library for a forward arrow.
            Icon(
                imageVector = BackArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp).rotate(180f),
            )
        }
    }
}

@Composable
private fun StoreLogo(store: Store) {
    if (store.logoUrl != null) {
        // Logos are drawn for a white background, so the tile stays white in the dark theme too.
        AsyncImage(
            model = store.logoUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(width = 72.dp, height = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(6.dp),
        )
    } else {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = store.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

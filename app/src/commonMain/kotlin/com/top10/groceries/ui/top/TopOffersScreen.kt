package com.top10.groceries.ui.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.groceries.di.AppContainer
import com.top10.groceries.domain.model.TopOffers
import com.top10.groceries.resources.Res
import com.top10.groceries.resources.empty_hint
import com.top10.groceries.resources.empty_title
import com.top10.groceries.resources.error_hint
import com.top10.groceries.resources.error_refresh
import com.top10.groceries.resources.error_title
import com.top10.groceries.resources.how_body
import com.top10.groceries.resources.how_title
import com.top10.groceries.resources.loading_hint
import com.top10.groceries.resources.loading_title
import com.top10.groceries.resources.refresh
import com.top10.groceries.resources.retry
import com.top10.groceries.resources.top_subtitle
import com.top10.groceries.resources.top_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopOffersRoute(container: AppContainer) {
    val viewModel = viewModel { TopOffersViewModel(container.getTopOffers) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    TopOffersScreen(
        state = state,
        onRefresh = viewModel::onRefresh,
        onOpenProduct = { url -> uriHandler.openUri(url) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopOffersScreen(
    state: TopOffersUiState,
    onRefresh: () -> Unit,
    onOpenProduct: (String) -> Unit,
) {
    val storeName = state.store.displayName
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.top_title, storeName))
                        state.top?.let { top ->
                            Text(
                                text = stringResource(Res.string.top_subtitle, top.consideredCount),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    if (state.top != null) {
                        TextButton(onClick = onRefresh, enabled = !state.isLoading) {
                            Text(stringResource(Res.string.refresh))
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val top = state.top
            when {
                top != null -> OfferList(
                    top = top,
                    isRefreshing = state.isLoading,
                    refreshFailed = state.loadFailed,
                    onOpenProduct = onOpenProduct,
                )

                state.loadFailed -> Message(
                    title = stringResource(Res.string.error_title),
                    hint = stringResource(Res.string.error_hint),
                ) {
                    Button(onClick = onRefresh) { Text(stringResource(Res.string.retry)) }
                }

                else -> Message(
                    title = stringResource(Res.string.loading_title, storeName),
                    hint = stringResource(Res.string.loading_hint),
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun OfferList(
    top: TopOffers,
    isRefreshing: Boolean,
    refreshFailed: Boolean,
    onOpenProduct: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (isRefreshing) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (top.offers.isEmpty()) {
            Message(
                title = stringResource(Res.string.empty_title),
                hint = stringResource(Res.string.empty_hint, top.store.displayName),
            )
            return@Column
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (refreshFailed) {
                item {
                    Text(
                        text = stringResource(Res.string.error_refresh),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            items(top.offers, key = { it.offer.productCode }) { ranked ->
                OfferCard(ranked = ranked, onOpenProduct = onOpenProduct)
            }
            item {
                Column(Modifier.padding(top = 12.dp, bottom = 24.dp)) {
                    Text(
                        text = stringResource(Res.string.how_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.how_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun Message(
    title: String,
    hint: String,
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        content()
    }
}

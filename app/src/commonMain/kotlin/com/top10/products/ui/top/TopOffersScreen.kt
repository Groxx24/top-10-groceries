package com.top10.products.ui.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.TopOffers
import com.top10.products.resources.Res
import com.top10.products.resources.back
import com.top10.products.resources.empty_hint
import com.top10.products.resources.empty_title
import com.top10.products.resources.error_hint
import com.top10.products.resources.error_title
import com.top10.products.resources.loading
import com.top10.products.resources.retry
import com.top10.products.resources.top_title
import com.top10.products.resources.top_title_loading
import com.top10.products.ui.BackArrow
import com.top10.products.ui.Message
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopOffersRoute(container: AppContainer, storeId: String, onBack: () -> Unit) {
    val viewModel = viewModel(key = storeId) { TopOffersViewModel(storeId, container.getTopOffers) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    TopOffersScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onOpenProduct = { url -> uriHandler.openUri(url) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopOffersScreen(
    state: TopOffersUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenProduct: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.top?.let { stringResource(Res.string.top_title, it.store.name) }
                            ?: stringResource(Res.string.top_title_loading),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(BackArrow, contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val top = state.top
            when {
                top != null -> OfferList(top = top, onOpenProduct = onOpenProduct)

                state.loadFailed -> Message(
                    title = stringResource(Res.string.error_title),
                    hint = stringResource(Res.string.error_hint),
                ) {
                    Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
                }

                else -> Message(title = stringResource(Res.string.loading)) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun OfferList(top: TopOffers, onOpenProduct: (String) -> Unit) {
    if (top.offers.isEmpty()) {
        Message(
            title = stringResource(Res.string.empty_title),
            hint = stringResource(Res.string.empty_hint, top.store.name),
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(top.offers, key = { it.productId }) { offer ->
            OfferCard(offer = offer, onOpenProduct = onOpenProduct)
        }
    }
}

package com.top10.products.ui.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.TopOffers
import com.top10.products.resources.Res
import com.top10.products.resources.empty_hint
import com.top10.products.resources.empty_title
import com.top10.products.resources.error_title
import com.top10.products.resources.top_title
import com.top10.products.resources.top_title_loading
import com.top10.products.ui.BackTopBar
import com.top10.products.ui.ErrorMessage
import com.top10.products.ui.LoadingMessage
import com.top10.products.ui.Message
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopOffersRoute(container: AppContainer, storeId: String, onBack: () -> Unit) {
    val viewModel = viewModel(key = storeId) { TopOffersViewModel(storeId, container.getTopOffers) }
    // Every time the screen opens, not only the first: the ViewModel is kept for the whole session.
    LaunchedEffect(viewModel) { viewModel.onOpened() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    TopOffersScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::onRetry,
    )
}

@Composable
fun TopOffersScreen(
    state: TopOffersUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = state.top?.let { stringResource(Res.string.top_title, it.store.name) }
                    ?: stringResource(Res.string.top_title_loading),
                onBack = onBack,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val top = state.top
            when {
                top != null -> OfferList(top)

                state.loadFailed -> ErrorMessage(stringResource(Res.string.error_title), onRetry)
                else -> LoadingMessage()
            }
        }
    }
}

@Composable
private fun OfferList(top: TopOffers) {
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
        items(top.offers, key = { it.rank }) { offer ->
            OfferCard(offer)
        }
    }
}

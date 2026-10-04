package com.top10.products.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.TOP_LIST_SIZE
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.resources.Res
import com.top10.products.resources.debug_deals_error_title
import com.top10.products.resources.debug_pick_top_title
import com.top10.products.resources.debug_pick_top_title_loading
import com.top10.products.resources.debug_selected_count
import com.top10.products.resources.debug_submit
import com.top10.products.resources.debug_submit_failed
import com.top10.products.resources.debug_submitted
import com.top10.products.ui.BackTopBar
import com.top10.products.ui.ErrorMessage
import com.top10.products.ui.LoadingMessage
import com.top10.products.ui.deal.DealInfo
import com.top10.products.ui.deal.DealPicture
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickTopRoute(container: AppContainer, storeId: String, onBack: () -> Unit) {
    val viewModel = viewModel(key = "pick-top-$storeId") {
        PickTopViewModel(storeId, container.getWeeklyDeals, container.submitTopList)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    PickTopScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onToggle = viewModel::onToggle,
        onSubmit = viewModel::onSubmit,
        onSubmitResultShown = viewModel::onSubmitResultShown,
    )
}

/** A store's 20 candidate deals; exactly [TOP_LIST_SIZE] of them make its top list. */
@Composable
fun PickTopScreen(
    state: PickTopUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggle: (dealId: String) -> Unit,
    onSubmit: () -> Unit,
    onSubmitResultShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    SubmitResultSnackbar(state.submit, snackbarHostState, onSubmitResultShown)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BackTopBar(
                title = state.candidates?.let { stringResource(Res.string.debug_pick_top_title, it.store.name) }
                    ?: stringResource(Res.string.debug_pick_top_title_loading),
                onBack = onBack,
            )
        },
        bottomBar = {
            if (state.candidates != null) {
                SubmitBar(
                    selectedCount = state.selectedIds.size,
                    canSubmit = state.canSubmit,
                    isSubmitting = state.submit == SubmitStatus.Submitting,
                    onSubmit = onSubmit,
                )
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val candidates = state.candidates
            when {
                candidates != null -> CandidateList(candidates.deals, state.selectedIds, state.isFull, onToggle)
                state.loadFailed -> ErrorMessage(stringResource(Res.string.debug_deals_error_title), onRetry)
                else -> LoadingMessage()
            }
        }
    }
}

/** Shows how the last submit went, once, then reports it shown through [onShown]. */
@Composable
private fun SubmitResultSnackbar(submit: SubmitStatus, snackbarHostState: SnackbarHostState, onShown: () -> Unit) {
    val submittedMessage = stringResource(Res.string.debug_submitted)
    val submitFailedMessage = stringResource(Res.string.debug_submit_failed)
    LaunchedEffect(submit) {
        val message = when (submit) {
            SubmitStatus.Submitted -> submittedMessage
            SubmitStatus.Failed -> submitFailedMessage
            SubmitStatus.Idle, SubmitStatus.Submitting -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(message)
        onShown()
    }
}

/** The deals to pick from; once [isFull], only the picked ones can be changed. */
@Composable
private fun CandidateList(
    deals: List<WeeklyDeal>,
    selectedIds: Set<String>,
    isFull: Boolean,
    onToggle: (dealId: String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(deals, key = { it.id }) { deal ->
            val selected = deal.id in selectedIds
            DealRow(
                deal = deal,
                selected = selected,
                enabled = selected || !isFull,
                onToggle = { onToggle(deal.id) },
            )
        }
    }
}

/** One candidate. Once the list is full, the ones not picked are [enabled] = false. */
@Composable
private fun DealRow(deal: WeeklyDeal, selected: Boolean, enabled: Boolean, onToggle: () -> Unit) {
    ElevatedCard(onClick = onToggle, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = selected, onCheckedChange = { onToggle() }, enabled = enabled)
            DealPicture(deal.category)
            DealInfo(deal, Modifier.weight(1f))
        }
    }
}

/** How many are picked, and the button that is enabled only at exactly [TOP_LIST_SIZE]. */
@Composable
private fun SubmitBar(selectedCount: Int, canSubmit: Boolean, isSubmitting: Boolean, onSubmit: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.debug_selected_count, selectedCount, TOP_LIST_SIZE),
                style = MaterialTheme.typography.titleSmall,
                color = if (selectedCount == TOP_LIST_SIZE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (isSubmitting) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            Button(onClick = onSubmit, enabled = canSubmit) { Text(stringResource(Res.string.debug_submit)) }
        }
    }
}

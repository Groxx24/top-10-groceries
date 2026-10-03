package com.top10.products.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.domain.model.TOP_LIST_SIZE
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.model.discount
import com.top10.products.resources.Res
import com.top10.products.resources.back
import com.top10.products.resources.deal_loyalty_card
import com.top10.products.resources.deal_percent_minus
import com.top10.products.resources.deal_price
import com.top10.products.resources.deal_valid_until
import com.top10.products.resources.debug_deals_error_title
import com.top10.products.resources.debug_pick_top_title
import com.top10.products.resources.debug_pick_top_title_loading
import com.top10.products.resources.debug_selected_count
import com.top10.products.resources.debug_submit
import com.top10.products.resources.error_hint
import com.top10.products.resources.loading
import com.top10.products.resources.retry
import com.top10.products.ui.BackArrow
import com.top10.products.ui.Message
import com.top10.products.ui.top.formatEuros
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun PickTopRoute(container: AppContainer, storeId: String, onBack: () -> Unit) {
    val viewModel = viewModel(key = "pick-top-$storeId") { PickTopViewModel(storeId, container.getWeeklyDeals) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    PickTopScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onToggle = viewModel::onToggle,
        onSubmit = viewModel::onSubmit,
    )
}

/** A store's 20 candidate deals; exactly [TOP_LIST_SIZE] of them make its top list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickTopScreen(
    state: PickTopUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggle: (dealId: String) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.candidates?.let { stringResource(Res.string.debug_pick_top_title, it.store.name) }
                            ?: stringResource(Res.string.debug_pick_top_title_loading),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(BackArrow, contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
        bottomBar = {
            if (state.candidates != null) {
                SubmitBar(selectedCount = state.selectedIds.size, canSubmit = state.canSubmit, onSubmit = onSubmit)
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val candidates = state.candidates
            when {
                candidates != null -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(candidates.deals, key = { it.id }) { deal ->
                        val selected = deal.id in state.selectedIds
                        DealRow(
                            deal = deal,
                            selected = selected,
                            enabled = selected || !state.isFull,
                            onToggle = { onToggle(deal.id) },
                        )
                    }
                }

                state.loadFailed -> Message(
                    title = stringResource(Res.string.debug_deals_error_title),
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
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = deal.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                listOfNotNull(deal.brand, deal.packageSize).takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // The folder's own wording; the percentage off only when it printed none.
                val label = deal.label
                    ?: deal.discount?.let { stringResource(Res.string.deal_percent_minus, (it * 100).roundToInt()) }
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                DealDetails(deal)
            }
        }
    }
}

/** "€3.09 /2 · Until 07/10 · Loyalty card needed", with only what the folder gives. */
@Composable
private fun DealDetails(deal: WeeklyDeal) {
    val details = listOfNotNull(
        deal.price?.let { price ->
            listOfNotNull(stringResource(Res.string.deal_price, formatEuros(price)), deal.priceUnit).joinToString(" ")
        },
        deal.validUntil?.let { stringResource(Res.string.deal_valid_until, it) },
        if (deal.needsLoyaltyCard) stringResource(Res.string.deal_loyalty_card) else null,
    )
    Text(
        text = details.joinToString(" · "),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** How many are picked, and the button that is enabled only at exactly [TOP_LIST_SIZE]. */
@Composable
private fun SubmitBar(selectedCount: Int, canSubmit: Boolean, onSubmit: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.debug_selected_count, selectedCount, TOP_LIST_SIZE),
                style = MaterialTheme.typography.titleSmall,
                color = if (canSubmit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Button(onClick = onSubmit, enabled = canSubmit) { Text(stringResource(Res.string.debug_submit)) }
        }
    }
}

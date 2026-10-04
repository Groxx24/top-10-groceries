package com.top10.deals.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.deals.di.AppContainer
import com.top10.deals.resources.Res
import com.top10.deals.resources.cancel
import com.top10.deals.resources.debug_delete_ended
import com.top10.deals.resources.debug_delete_ended_confirm
import com.top10.deals.resources.debug_delete_ended_confirm_hint
import com.top10.deals.resources.debug_delete_ended_confirm_title
import com.top10.deals.resources.debug_delete_ended_failed
import com.top10.deals.resources.debug_delete_ended_hint
import com.top10.deals.resources.debug_delete_ended_none
import com.top10.deals.resources.debug_deleted_ended
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
    val deleteEndedViewModel = viewModel { DeleteEndedViewModel(container.deleteEndedTopLists) }
    val deleteEnded by deleteEndedViewModel.status.collectAsStateWithLifecycle()
    DebugScreen(
        state = state,
        deleteEnded = deleteEnded,
        onRetry = viewModel::onRetry,
        onPickStore = onPickStore,
        onDeleteEnded = deleteEndedViewModel::onDelete,
        onDeleteEndedResultShown = deleteEndedViewModel::onResultShown,
        onBack = onBack,
    )
}

@Composable
fun DebugScreen(
    state: StoresUiState,
    deleteEnded: DeleteEndedStatus,
    onRetry: () -> Unit,
    onPickStore: (storeId: String) -> Unit,
    onDeleteEnded: () -> Unit,
    onDeleteEndedResultShown: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    DeleteEndedResultSnackbar(deleteEnded, snackbarHostState, onDeleteEndedResultShown)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { BackTopBar(stringResource(Res.string.debug_title), onBack) },
        bottomBar = { DeleteEndedBar(isDeleting = deleteEnded == DeleteEndedStatus.Deleting, onDelete = onDeleteEnded) },
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

/** Shows how the last delete went, once, then reports it shown through [onShown]. */
@Composable
private fun DeleteEndedResultSnackbar(status: DeleteEndedStatus, snackbarHostState: SnackbarHostState, onShown: () -> Unit) {
    val failedMessage = stringResource(Res.string.debug_delete_ended_failed)
    val noneMessage = stringResource(Res.string.debug_delete_ended_none)
    val deletedMessage = (status as? DeleteEndedStatus.Deleted)?.storeNames?.takeIf { it.isNotEmpty() }
        ?.let { stringResource(Res.string.debug_deleted_ended, it.joinToString(", ")) }
    LaunchedEffect(status) {
        val message = when (status) {
            is DeleteEndedStatus.Deleted -> deletedMessage ?: noneMessage
            DeleteEndedStatus.Failed -> failedMessage
            DeleteEndedStatus.Idle, DeleteEndedStatus.Deleting -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(message)
        onShown()
    }
}

/** What the clean-up does, and its button, which asks before deleting anything. */
@Composable
private fun DeleteEndedBar(isDeleting: Boolean, onDelete: () -> Unit) {
    var confirming by rememberSaveable { mutableStateOf(false) }
    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text(stringResource(Res.string.debug_delete_ended_confirm_title)) },
            text = { Text(stringResource(Res.string.debug_delete_ended_confirm_hint)) },
            confirmButton = {
                TextButton(onClick = {
                    confirming = false
                    onDelete()
                }) {
                    Text(stringResource(Res.string.debug_delete_ended_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirming = false }) { Text(stringResource(Res.string.cancel)) }
            },
        )
    }
    Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.debug_delete_ended_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (isDeleting) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            OutlinedButton(onClick = { confirming = true }, enabled = !isDeleting) {
                Text(stringResource(Res.string.debug_delete_ended))
            }
        }
    }
}

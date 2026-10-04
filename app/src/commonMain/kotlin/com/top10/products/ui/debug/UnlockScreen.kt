package com.top10.products.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.top10.products.di.AppContainer
import com.top10.products.resources.Res
import com.top10.products.resources.debug_title
import com.top10.products.resources.unlock_button
import com.top10.products.resources.unlock_hint
import com.top10.products.resources.unlock_passphrase
import com.top10.products.resources.unlock_title
import com.top10.products.resources.unlock_wrong
import com.top10.products.ui.BackTopBar
import org.jetbrains.compose.resources.stringResource

/** Asks for the passphrase before the debug screens; [onUnlocked] once it is right. */
@Composable
fun UnlockRoute(container: AppContainer, onUnlocked: () -> Unit, onBack: () -> Unit) {
    val viewModel = viewModel { UnlockViewModel(container.unlockDebug) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.unlocked) {
        if (state.unlocked) {
            viewModel.onUnlockHandled()
            onUnlocked()
        }
    }
    UnlockScreen(state = state, onUnlock = viewModel::onUnlock, onBack = onBack)
}

@Composable
fun UnlockScreen(state: UnlockUiState, onUnlock: (passphrase: String) -> Unit, onBack: () -> Unit) {
    // Kept only while the screen is shown, never saved.
    var passphrase by remember { mutableStateOf("") }
    val submit = { onUnlock(passphrase) }
    Scaffold(topBar = { BackTopBar(stringResource(Res.string.debug_title), onBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.unlock_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.unlock_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PassphraseField(
                passphrase = passphrase,
                onChange = { passphrase = it },
                enabled = !state.isChecking,
                isWrong = state.wrongPassphrase,
                onDone = submit,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = submit, enabled = !state.isChecking && passphrase.isNotEmpty()) {
                    Text(stringResource(Res.string.unlock_button))
                }
                if (state.isChecking) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        }
    }
}

/** A hidden, single-line password field; [isWrong] marks it after a wrong guess. */
@Composable
private fun PassphraseField(
    passphrase: String,
    onChange: (String) -> Unit,
    enabled: Boolean,
    isWrong: Boolean,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        value = passphrase,
        onValueChange = onChange,
        label = { Text(stringResource(Res.string.unlock_passphrase)) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        isError = isWrong,
        supportingText = if (isWrong) {
            { Text(stringResource(Res.string.unlock_wrong)) }
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

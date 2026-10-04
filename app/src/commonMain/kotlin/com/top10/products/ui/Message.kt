package com.top10.products.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.top10.products.resources.Res
import com.top10.products.resources.error_hint
import com.top10.products.resources.loading
import com.top10.products.resources.retry
import org.jetbrains.compose.resources.stringResource

/** A centred title and hint filling the screen, for loading, empty and error states. */
@Composable
fun Message(
    title: String,
    hint: String? = null,
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        hint?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        content()
    }
}

@Composable
fun LoadingMessage() {
    Message(title = stringResource(Res.string.loading)) {
        CircularProgressIndicator()
    }
}

/** Loading failed: [title] says what, with a button to [onRetry]. */
@Composable
fun ErrorMessage(title: String, onRetry: () -> Unit) {
    Message(title = title, hint = stringResource(Res.string.error_hint)) {
        Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
    }
}

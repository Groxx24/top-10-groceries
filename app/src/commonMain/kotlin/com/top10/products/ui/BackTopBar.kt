package com.top10.products.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.top10.products.resources.Res
import com.top10.products.resources.back
import org.jetbrains.compose.resources.stringResource

/** The top bar of every screen below the store list: its [title] and a back arrow. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(BackArrow, contentDescription = stringResource(Res.string.back))
            }
        },
    )
}

package com.guillotine.jscorekeeper.composable.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guillotine.jscorekeeper.R

@Composable
fun DeleteAllGamesDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = stringResource(R.string.warning_icon_description)
            )
        },
        title = {
            Text(text = stringResource(R.string.delete_all_games))
        },
        text = {
            Text(text = stringResource(R.string.delete_all_games_warning))
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

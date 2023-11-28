package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.reportActivity(),
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.error,
                    contentDescription = "error",
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.failed),
                    style = MaterialTheme.typography.h6.copy(
                        color = MaterialTheme.colors.error,
                        fontSize = 24.sp,
                    ),
                )
            }
        },
        text = {
            Text(
                error,
                style = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.ok),
                )
            }
        },
    )
}

@Preview
@Composable
fun ErrorDialogPreview() {
    LedgerGreenTheme {
        ErrorDialog(error = "Something went wrong. Try again later") { }
    }
}

package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.supportdialog.SupportDialog

@Composable
fun SupportButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.support),
) {
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        SupportDialog(
            onDismiss = {
                openDialog.value = false
            },
        )
    }

    TextButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(Color.White),
        content = { Text(text, color = Color(0xFF06478D)) },
        onClick = {
            openDialog.value = true
        },
    )
}

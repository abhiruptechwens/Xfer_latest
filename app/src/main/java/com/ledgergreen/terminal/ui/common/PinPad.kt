@file:OptIn(ExperimentalMaterialApi::class)

package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.ui.common.ledgergreenicons.IcBackspace
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun PinPad(
    onValueChange: (Int) -> Unit,
    onReset: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        PinPadRow(Modifier.padding(top = 4.dp)) {
            PinPadButton(value = 1, enabled = enabled, onClick = { onValueChange(1) })
            PinPadButton(value = 2, enabled = enabled, onClick = { onValueChange(2) })
            PinPadButton(value = 3, enabled = enabled, onClick = { onValueChange(3) })
        }
        PinPadRow(Modifier.padding(top = 4.dp)) {
            PinPadButton(value = 4, enabled = enabled, onClick = { onValueChange(4) })
            PinPadButton(value = 5, enabled = enabled, onClick = { onValueChange(5) })
            PinPadButton(value = 6, enabled = enabled, onClick = { onValueChange(6) })
        }
        PinPadRow(Modifier.padding(top = 4.dp)) {
            PinPadButton(value = 7, enabled = enabled, onClick = { onValueChange(7) })
            PinPadButton(value = 8, enabled = enabled, onClick = { onValueChange(8) })
            PinPadButton(value = 9, enabled = enabled, onClick = { onValueChange(9) })
        }
        PinPadRow(Modifier.padding(top = 4.dp)) {
            PinPadSpecialButton(enabled = enabled, onClick = onReset) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                )
            }
            PinPadButton(value = 0, enabled = enabled, onClick = { onValueChange(0) })
            PinPadSpecialButton(enabled = enabled, onClick = onBackspace) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),
                    imageVector = LedgerGreenIcons.IcBackspace,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun PinPadRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        content = content
    )
}

private fun Modifier.pinPadButtonModifier(): Modifier {
    return this
        .height(64.dp)
        .padding(horizontal = 4.dp)
}

@Composable
private fun RowScope.PinPadButton(
    value: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .pinPadButtonModifier()
            .weight(1f),
        enabled = enabled,
        elevation = 8.dp,
        onClick = onClick,
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = value.toString(),
                style = MaterialTheme.typography.button.copy(
                    fontSize = 24.sp,
                )
            )
        }
    }
}

@Composable
private fun RowScope.PinPadSpecialButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .pinPadButtonModifier()
            .weight(1f),
        enabled = enabled,
        elevation = 8.dp,
        onClick = onClick,
    ) {
        Box(Modifier.fillMaxSize()) {
            content()
        }
    }
}

@NexgoN6Preview
@Composable
fun PinPadPreview() {
    LedgerGreenTheme {
        PinPad(
            onValueChange = {},
            onReset = {},
            onBackspace = {},
        )
    }
}

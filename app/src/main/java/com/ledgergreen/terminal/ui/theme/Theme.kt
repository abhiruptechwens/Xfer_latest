package com.ledgergreen.terminal.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LedgerGreenTheme(
    content: @Composable () -> Unit,
) {
    val colors = appColors

    val typography = Typography()

    MaterialTheme(
        colors = colors,
        content = content,
        shapes = Shapes(),
        typography = typography
            .copy(
                button = typography.button.copy(
                    fontWeight = FontWeight.Black,
                ),
            ),
    )
}

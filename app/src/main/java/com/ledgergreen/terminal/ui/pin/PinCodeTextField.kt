package com.ledgergreen.terminal.ui.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinCodeTextField(
    pinCode: String,
    enabled: Boolean,
    onPinCodeChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pinCodeCount: Int = 4,
) {
    BasicTextField(
        modifier = modifier,
        value = pinCode,
        enabled = enabled,
        readOnly = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = Color.White
        ),
        onValueChange = {
            if (it.length <= pinCodeCount) {
                onPinCodeChange(it, it.length == pinCodeCount)
            }
        },
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(pinCodeCount) { index ->
                    CharView(
                        index = index,
                        text = pinCode,
                    )
                }
            }
        },
    )
}

@Composable
fun CharView(
    index: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> "*"
    }
    Text(
        modifier = Modifier
            .width(64.dp)
            .bottomBorder(1.dp, MaterialTheme.colors.onBackground)
            .padding(2.dp)
            .then(modifier),
        text = char,
        style = MaterialTheme.typography.h4,
        color = MaterialTheme.colors.onPrimary,
        textAlign = TextAlign.Center,
    )
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = Color.White,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx,
            )
        }
    },
)

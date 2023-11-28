package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.ledgergreen.terminal.idle.LocalIdleLocker

fun Boolean.toVisibility() = if (this) 1f else 0f

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.reportActivity(): Modifier =
    composed {
        val idleLocker = LocalIdleLocker.current

        this.pointerInteropFilter(
            onTouchEvent = {
                idleLocker?.onActivityDetected()
                false
            },
        )
    }

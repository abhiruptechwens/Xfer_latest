package com.ledgergreen.terminal.ui.autolockdialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.idle.IdleLocker
import com.ledgergreen.terminal.ui.common.noRippleClickable
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import kotlin.math.roundToInt

@Composable
fun AutolockDialog(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val progressAnimationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(
                IdleLocker.autolockDialogDurationMs.toInt(),
                easing = LinearEasing,
            ),
        ),
    )

    AutolockDialog(
        progress = progressAnimationValue,
        onClose = onClose,
        modifier = modifier,
    )
}

@Composable
fun AutolockDialog(
    progress: Float,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dialogDurationSec = IdleLocker.autolockDialogDurationMs / 1000

    val secondsLeft = (dialogDurationSec -
        dialogDurationSec * progress).roundToInt()

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = modifier.noRippleClickable(onClose),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.autolock_title),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))

                Box(contentAlignment = Alignment.Center)
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(180.dp),
                        progress = progress,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 8.dp,
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    )
                    {
                        Text(
                            text = stringResource(R.string.autolock_in),
                            style = MaterialTheme.typography.h6,
                        )
                        Text(
                            text = stringResource(
                                id = R.string.time_number_sec,
                                secondsLeft,
                            ),
                            style = MaterialTheme.typography.h6,
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.autolock_prevent_instruction),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
    }
}

@Preview
@Composable
fun AutolockDialog10secPreview() {
    LedgerGreenTheme {
        AutolockDialog(
            progress = 0f,
            onClose = { },
        )
    }
}

@Preview
@Composable
fun AutolockDialog1secPreview() {
    LedgerGreenTheme {
        AutolockDialog(
            progress = 0.9f,
            onClose = { },
        )
    }
}

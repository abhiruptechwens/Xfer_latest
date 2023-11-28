package com.ledgergreen.terminal.ui.home.dialogs

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

private const val barcodeHeight = 90f
private const val scanBarHeight = 3f

@Composable
fun DocScanActiveDialog(
    onScanCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val infiniteTransition = rememberInfiniteTransition(label = "scan_bar")

    val scanIndicatorPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = barcodeHeight - scanBarHeight / 2,
        infiniteRepeatable(
            animation = tween(
                2000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan_bar_height",
    )

    DocScanActiveDialog(
        onScanCancel = onScanCancel,
        modifier = modifier,
        scanIndicatorPosition = scanIndicatorPosition,
    )
}

@Composable
fun DocScanActiveDialog(
    onScanCancel: () -> Unit,
    scanIndicatorPosition: Float,
    modifier: Modifier = Modifier,
) {


    Dialog(
        onDismissRequest = onScanCancel,
    ) {
        Surface(modifier.reportActivity()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.scan_document).titleCase(),
                    style = MaterialTheme.typography.h6,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.point_the_optical_sensor_at_barcode),
                    style = MaterialTheme.typography.body1,
                )
                Spacer(Modifier.height(16.dp))
                BoxWithConstraints(modifier = Modifier.height(barcodeHeight.dp)) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.barcode_example),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                    )

                    Box(
                        modifier = Modifier
                            .absoluteOffset(y = scanIndicatorPosition.dp)
                            .fillMaxWidth()
                            .height(scanBarHeight.dp)
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(8.dp),
                            ),
                    )

                }
                Spacer(Modifier.height(16.dp))
                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onScanCancel,
                    content = { Text("Cancel") },
                )

            }
        }
    }
}

@NexgoN6Preview
@Composable
fun DocScanActiveDialogPreview() {
    LedgerGreenTheme {
        DocScanActiveDialog(
            onScanCancel = { },
        )
    }
}

package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppStateViewModel

@Composable
fun AppIcon(
    logoUrl: String,
    modifier: Modifier = Modifier,
    navigateToHome : () -> Unit,
    logout: () -> Unit
) {
    Image(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    navigateToHome()
                },
                onLongPress = {
                    logout()
                }
            )
        },
        painter = rememberAsyncImagePainter(logoUrl),
//        painter = painterResource(id = logoUrl.toInt()),
        contentDescription = "app icon",

    )
}

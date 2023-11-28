package com.ledgergreen.terminal.ui.common

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
private fun BackPress() {
    var showToast by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPressSealed>(BackPressSealed.Idle) }
    val context = LocalContext.current

    if (showToast) {
        Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    LaunchedEffect(key1 = backPressState) {
        if (backPressState == BackPressSealed.InitialTouch) {
            delay(2000)
            backPressState = BackPressSealed.Idle
        }
    }

    BackHandler(backPressState == BackPressSealed.Idle) {
        backPressState = BackPressSealed.InitialTouch
        showToast = true
    }
}

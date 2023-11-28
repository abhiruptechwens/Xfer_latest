package com.ledgergreen.terminal.ui.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.AppIcon
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PinPad
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.PinAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun PinScreen(
    modifier: Modifier = Modifier,
    viewModel: PinViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    PinScreen(
        state = state,
        onPinChange = { pinCode, finish ->
            viewModel.onPinChanged(pinCode, finish)
        },
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
    )
}

@Composable
fun PinScreen(
    state: PinState,
    onPinChange: (pinCode: String, finish: Boolean) -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {
    state.error?.let {
        ErrorDialog(it, state.onErrorShown)
    }
    val darkBlue = 0xFF06478D
    val lightBlue = "#005CE1".toColorInt()


    Scaffold(
        modifier = modifier,
        backgroundColor = Color(0xFF06478D),
        topBar = {
            PinAppBar(
                logoUrl = appBarConfig.logoUrl,
                onLogout = appBarConfig.onLogout,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


//            Image(
//                modifier = modifier.padding(top = 50.dp).size(100.dp),
//                painter = painterResource(id = R.drawable.xfericon),
//                contentDescription = "app icon",
//            )
//            Text(
//                "Terminal Login",
//                color = Color.White,
//                modifier = Modifier.padding(top = 5.dp),
//            )
            Spacer(Modifier.weight(5f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                PinCodeTextField(
                    modifier = Modifier.focusable(enabled = false),
                    pinCode = state.pinCode,
                    enabled = true,
                    onPinCodeChange = onPinChange,
                )

                if (state.isLoading) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PinPad(
                modifier = Modifier.padding(16.dp),
                enabled = !state.isLoading,
                onValueChange = {
                    val newValue = state.pinCode + it
                    val finish = newValue.length == 4
                    onPinChange(newValue, finish)
                },
                onReset = { onPinChange("", false) },
                onBackspace = {
                    val newValue = state.pinCode.dropLast(1)
                    onPinChange(newValue, false)
                },
            )

            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = state.appInfo,
                color = Color.White,
                style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.onBackground,
                ),
            )
        }
    }
}

@NexgoN6Preview
@Composable
fun PinCodeScreenPreview() {
    LedgerGreenTheme {
        PinScreen(
            state = PinState(
                vendorName = "Veritas Pay",
                pinCode = "123",
                isLoading = false,
                appInfo = "Version 1.0 code 10",
                error = null,
                success = false,
                onErrorShown = {},
            ),
            onPinChange = { _, _ -> },
            appBarConfig = DefaultAppBarConfig.preview,
        )
    }
}

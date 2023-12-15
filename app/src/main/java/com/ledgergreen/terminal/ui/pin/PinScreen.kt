package com.ledgergreen.terminal.ui.pin

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.data.network.model.Terminal
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
        backgroundColor = Color.White,
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

            if (state.companyId == 132.toLong()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(start = 16.dp),
//                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        color = Color.Black,
                        text = "Terminal:",
                        fontSize = 12.sp,
                    )
                    Text(
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp),
//                        text = state.terminal!!.name,
                        text = state.terminal!!.name,
                        fontSize = 12.sp,
                    )
                }
            }


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
            Spacer(Modifier.weight(2f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = state.appInfo,
                color = Color.Black,
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
                terminal = Terminal(
                    terminalId = "R123",
                    id = 123,
                    name = "r1",
                    slug = "Something"
                    ),
                companyId = 1,
            ),
            onPinChange = { _, _ -> },
            appBarConfig = DefaultAppBarConfig.preview,
        )
    }
}

package com.ledgergreen.terminal.ui.pinreset

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PinPad
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.PinAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.pin.PinCodeTextField
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun ChangePinScreen(
    modifier: Modifier = Modifier,
    viewModel: ChangePinViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    ChangePinScreen(
        modifier = modifier,
        versionInfo = state.versionInfo,
        pin1 = state.pin1,
        pin2 = state.pin2,
        loading = state.loading,
        error = state.error,
        onPin1Change = { pin, _ ->
            state.eventSink(ChangePinEvent.Pin1Changed(pin))
        },
        onPin2Change = { pin, finish ->
            state.eventSink(ChangePinEvent.Pin2Changed(pin))
            if (finish) {
                state.eventSink(ChangePinEvent.ChangePin)
            }
        },
        onErrorShown = {
            state.eventSink(ChangePinEvent.ErrorShown)
        },
        appBarConfig = defaultAppBarConfig(),
    )
}

@Composable
fun ChangePinScreen(
    versionInfo: String,
    pin1: String,
    pin2: String,
    loading: Boolean,
    error: String?,
    onPin1Change: (String, Boolean) -> Unit,
    onPin2Change: (String, Boolean) -> Unit,
    onErrorShown: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
    pinLength: Int = 4,
) {
    val isFirstStep = pin1.length < pinLength
    BackHandler(enabled = !isFirstStep) {
        onPin1Change("", false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            PinAppBar(
                appBarConfig.logoUrl,
                appBarConfig.onLogout,
            )
        },
    ) { paddingValues ->
        error?.let {
            ErrorDialog(it, onErrorShown)
        }

        if (isFirstStep) {
            StepContent(
                pinCode = pin1,
                onPinChange = onPin1Change,
                label = stringResource(R.string.set_new_pin),
                pinLength = pinLength,
                loading = loading,
                versionInfo = versionInfo,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            )
        } else {
            StepContent(
                pinCode = pin2,
                onPinChange = onPin2Change,
                label = stringResource(R.string.confirm_new_pin),
                pinLength = pinLength,
                loading = loading,
                versionInfo = versionInfo,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
fun StepContent(
    pinCode: String,
    onPinChange: (value: String, finish: Boolean) -> Unit,
    label: String,
    loading: Boolean,
    pinLength: Int,
    versionInfo: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 24.dp),
        )
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            PinCodeTextField(
                modifier = Modifier.focusable(enabled = false),
                pinCode = pinCode,
                enabled = !loading,
                onPinCodeChange = onPinChange,
            )

            if (loading) {
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PinPad(
            modifier = Modifier.padding(16.dp),
            onValueChange = {
                val newValue = pinCode + it
                val finish = newValue.length == pinLength
                onPinChange(newValue, finish)
            },
            onReset = { onPinChange("", false) },
            onBackspace = {
                val newValue = pinCode.dropLast(1)
                onPinChange(newValue, false)
            },
            enabled = !loading,
        )

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = versionInfo,
            style = MaterialTheme.typography.caption.copy(
                color = MaterialTheme.colors.onBackground,
            ),
        )
    }
}

@NexgoN6Preview
@Composable
fun ChangePinScreenPreview() {
    LedgerGreenTheme {
        ChangePinScreen(
            versionInfo = "1.0.6 (34)",
            pin1 = "1234",
            pin2 = "1234",
            loading = false,
            error = null,
            onPin1Change = { _, _ -> },
            onPin2Change = { _, _ -> },
            onErrorShown = { },
            appBarConfig = DefaultAppBarConfig.preview,
            modifier = Modifier,
        )
    }
}

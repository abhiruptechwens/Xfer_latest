package com.ledgergreen.terminal.ui.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.SupportButton
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    state.errorMessage?.let {
        ErrorDialog(it) {
            state.eventSink(ChangePasswordEvent.OnErrorShown)
        }
    }

    ChangePasswordScreen(
        state = state,
        onPassword1Change = { state.eventSink(ChangePasswordEvent.Password1Change(it)) },
        onPassword2Change = { state.eventSink(ChangePasswordEvent.Password2Change(it)) },
        onChangePasswordClick = { state.eventSink(ChangePasswordEvent.ChangePassword) },
        modifier = modifier,
    )
}

@Composable
fun ChangePasswordScreen(
    state: ChangePasswordState,
    onPassword1Change: (String) -> Unit,
    onPassword2Change: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier, backgroundColor = Color(0xFF06478D)) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SupportButton(
                modifier = Modifier.align(Alignment.End),
            )
            Text(stringResource(R.string.reset_password), style = MaterialTheme.typography.h5, color = Color.White)
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                value = state.password1,
                onValueChange = onPassword1Change,
                label = stringResource(R.string.new_password),
                imeAction = ImeAction.Next,
                enabled = !state.loading,
                error = state.fieldValidationError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                value = state.password2,
                onValueChange = onPassword2Change,
                label = stringResource(R.string.confirm_new_password),
                imeAction = ImeAction.Done,
                enabled = !state.loading,
                error = state.fieldValidationError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = !state.loading,
                colors = ButtonDefaults.buttonColors(Color(0xE2FFFFFF)),
                onClick = onChangePasswordClick,
                content = {
                    Text(stringResource(R.string.change_password), color = Color(0xE2083364))
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.alpha(state.loading.toVisibility()),
            )
        }
    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    enabled: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
) {

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.White, // Set the focused outline color
        unfocusedBorderColor = Color.Black, // Set the unfocused outline color
        cursorColor = Color.White,
        textColor = Color.White// Set the cursor color
    )
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        visualTransformation = PasswordVisualTransformation(),
        label = { Text(error ?: label,color = Color.White) },
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
        ),
        colors = colors,
        maxLines = 1,
        singleLine = true,
        isError = error != null,
        enabled = enabled,
    )
}

@Composable
@NexgoN6Preview
fun ChangePasswordScreenPreview() {
    LedgerGreenTheme {
        ChangePasswordScreen(
            state = ChangePasswordState(
                password1 = "pass1",
                password2 = "pass1",
                loading = false,
                errorMessage = null,
                fieldValidationError = null,
                eventSink = { },
            ),
            onChangePasswordClick = { },
            onPassword1Change = { },
            onPassword2Change = { },
        )
    }
}

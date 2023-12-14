package com.ledgergreen.terminal.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PasswordTextField
import com.ledgergreen.terminal.ui.common.TextFieldWithUnderlineAndTrailingIcon
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    LoginScreen(
        state = state,
        onLoginChange = viewModel::onLoginChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLogin,
        modifier = modifier,
    )
}

@Composable
fun ShowChangelogDialog(onDismiss: () -> Unit) {
    val changelog = stringResource(id = R.string.changelogs)
    var showDialog by remember { mutableStateOf(true) }
//    val alertDialogBuilder = android.app.AlertDialog.Builder(LocalContext.current)
//    alertDialogBuilder
//        .setTitle("Changelog:")
//        .setMessage(changelog)
//        .setPositiveButton("OK") { dialog, _ ->
//            // Positive button click
//            dialog.dismiss()
//        }
//
//    val alertDialog = alertDialogBuilder.create()
//    alertDialog.show()


    if (showDialog && AppState1.lastVersionCode != 1)
        Dialog(
            onDismissRequest = { onDismiss() },
        ) {
            Surface(modifier = Modifier.reportActivity(), shape = RoundedCornerShape(15.dp)) {

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Changelog",
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(top = 16.dp),
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 20.dp)
                            .height(1.dp) // You can adjust the height of the line
                            .background(Color.Gray),
                    )

                    Column() {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Text(
                                "Version:",
                                modifier = Modifier.padding(start = 5.dp),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Right,
                            )
//                Spacer(Modifier.height(16.dp))
                            Text(
                                BuildConfig.VERSION_NAME,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                textAlign = TextAlign.End,
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = changelog,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start,
                        )


                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .height(50.dp),
                            onClick = {
                                showDialog = false
                                onDismiss()
                                AppState1.lastVersionCode = BuildConfig.VERSION_CODE
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFF003A8C)),
                            shape = RoundedCornerShape(15.dp),
                            content = { Text("Ok", color = Color.White) },
                        )
                    }
                }


            }
        }


}

@Composable
fun LoginScreen(
    state: LoginState,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    state.error?.let {
        ErrorDialog(it, state.onErrorShown)
    }

    if (BuildConfig.VERSION_CODE > AppState1.lastVersionCode)
        ShowChangelogDialog({})

    Scaffold(
        modifier = modifier.fillMaxSize(),
        backgroundColor = Color.White,
    ) { paddingValues ->

        Box {

            Image(painter = painterResource(id = R.drawable.top_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
//            SupportButton(
//                modifier = Modifier.align(Alignment.End),
//            )

                Image(
                    modifier = modifier
                        .padding(top = 50.dp)
                        .size(100.dp),
                    painter = painterResource(id = R.drawable.xfericon),
                    contentDescription = "app icon",
                )
                Text(
                    stringResource(R.string.terminal_login),
                    color = Color(0xFF001A4B),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))

                val colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF4D4D4D), // Set the focused outline color
                    unfocusedBorderColor = Color(0xFF4D4D4D), // Set the unfocused outline color
                    cursorColor = Color(0xFF4D4D4D),
                    textColor = Color(0xFF4D4D4D),// Set the cursor color
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).align(Alignment.Start),
                    text = "Enter Username",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Black,
                    ),
                )

                TextFieldWithUnderlineAndTrailingIcon(
                    value = state.login,
                    onValueChange = onLoginChange,
                    label = "Username",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next,
                        autoCorrect = false,
                    ),
                    iconResId = R.drawable.person_icon,
                    modifier = Modifier.fillMaxWidth()
                )

//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = state.login,
//                    onValueChange = onLoginChange,
//                    enabled = !state.isLoading,
//                    label = { Text(stringResource(R.string.login), color = Color(0xFF4D4D4D)) },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Ascii,
//                        imeAction = ImeAction.Next,
//                        autoCorrect = false,
//                    ),
//                    colors = colors,
//                    trailingIcon = {
//                        Icon(
//                            Icons.Default.Person, contentDescription = null,
//                            tint = Color(
//                                0xFF0043A5,
//                            ),
//                        )
//                    },
//                )
                Spacer(Modifier.height(25.dp))

                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).align(Alignment.Start),
                    text = "Enter Password",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Black,
                    ),
                )

                TextFieldWithUnderlineAndTrailingIcon(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                    ),
                    iconTap = true,
                    iconResId = R.drawable.eye_off,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
//                PasswordTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    password = state.password,
//                    enabled = !state.isLoading,
//                    onValueChange = onPasswordChange,
//                    error = state.error,
//                    label = stringResource(R.string.password),
//                    imeAction = ImeAction.Done,
//                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 25.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF6DA9FF),
                            shape = RoundedCornerShape(size = 5.dp),
                        )
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(
                            color = Color(0xE80043A5),
                            shape = RoundedCornerShape(size = 5.dp)
                        )
                        .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
                        .clickable {
                            onLoginClick()
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically),
                        text = stringResource(R.string.login),
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight(600),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        ),
                    )

                }

//            Button(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp),
//                onClick = onLoginClick,
//                enabled = !state.isLoading,
//                colors = ButtonDefaults.buttonColors(Color(0xFF61BAF5)),
//                content = { Text(stringResource(R.string.login),color = Color.White) },
//            )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.alpha(state.isLoading.toVisibility()),
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    text = stringResource(R.string.xfer_powered_by_west_town_bank_trust),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),

                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline,
                    ),
                )
            }
        }
    }
}


@NexgoN6Preview
@Composable
fun LoginScreenPreview() {
    LedgerGreenTheme {
        LoginScreen(
            state = LoginState(
                login = "terminal1",
                password = "password_123",
                isLoading = false,
                error = null,
                success = false,
                onErrorShown = {},
            ),
            onLoginChange = {},
            onPasswordChange = {},
            onLoginClick = {},
        )
    }
}

@NexgoN6Preview
@Composable
fun LoginScreenLoadingPreview() {
    LedgerGreenTheme {
        LoginScreen(
            state = LoginState(
                login = "terminal1",
                password = "password_123",
                isLoading = true,
                error = null,
                success = false,
                onErrorShown = {},
            ),
            onLoginChange = {},
            onPasswordChange = {},
            onLoginClick = {},
        )
    }
}

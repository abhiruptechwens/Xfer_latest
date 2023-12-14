package com.ledgergreen.terminal.ui.contactless

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.model.phone.CountryCodes
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.domain.ContactlessPayment
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.qr.QrGenerator
import com.ledgergreen.terminal.ui.agreement.addDotAfterLastTwoDigits
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PhoneNumberTextFieldWithLeadingIcon
import com.ledgergreen.terminal.ui.common.PhoneTextField
import com.ledgergreen.terminal.ui.common.TextFieldWithUnderlineAndTrailingIcon
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.ReadOnlyAmountTextField
import kotlin.math.round

@Composable
fun ContactlessScreen(
    navigateToRecentTransactions: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactlessViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    if (state.contactlessPayment?.smsSent == true) {
        LaunchedEffect("contactless_exit") {
            viewModel.clearTransaction()
//            navigateToRecentTransactions()
        }
    }

    ContactlessScreen(
        state = state,
        onPhoneChanged = viewModel::onPhoneChanged,
        onCountryCodeChanged = viewModel::onCountryCodeChanged,
        onCustomerNameChange = viewModel::onCustomerNameChanged,
        onSendSms = trackClick(
            targetName = Clicks.sendContactlessViaSMS,
            onClick = viewModel::onSendSms,
        ),
        onGenerateQrCode = trackClick(
            targetName = Clicks.sendContactlessViaQR,
            onClick = viewModel::onGenerateQrCode,
        ),
        appBarConfig = defaultAppBarConfig(),
        onFinish = {
            viewModel.clearTransaction()
            navigateToHome()
//            navigateToRecentTransactions()
        },

        onNavigateToHome = {
            navigateToHome()
        },
        navigateToHome = {
//            viewModel.onNavigateConsumed()
            if (state.contactlessPayment?.smsSent == true) {
                viewModel.clearTransaction()
                navigateToHome()
            }
        },
        modifier = modifier,
    )
}

@Composable
fun ContactlessScreen(
    onNavigateToHome:()-> Unit,
    navigateToHome: () -> Unit,
    state: ContactlessState,
    onPhoneChanged: (String) -> Unit,
    onCountryCodeChanged: (CountryPhoneCode) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onSendSms: () -> Unit,
    onGenerateQrCode: () -> Unit,
    onFinish: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {
    if (state.error != null) {
        ErrorDialog(state.error, state.onErrorShown)
    }

    if (state.contactlessPayment?.smsSent == true) {
        PaymentSuccessfulDialog(
            from = "fromContactless",
            amount = state.amount.toCurrencyString(),
            onPaymentDone = { navigateToHome() },
            onReject = { true },
        )
//            navigateToRecentTransactions()
    }

    state.contactlessPayment?.let {
        if (!it.smsSent) {
            ContactlessPaymentSuccessDialog(
                it,
                onDismiss = { onFinish() },
            )
        }
    }

    Scaffold(
        modifier = Modifier,
        backgroundColor = Color.White,
        topBar = {
            SwitchAppBar(appBarConfig, onNavigateToHome, {})
        },
    ) { paddingValues ->

        Box {

            Image(
                painter = painterResource(id = R.drawable.botton_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart)
            )


            Column {


                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(start = 16.dp, end = 16.dp, top = 5.dp)
                        .verticalScroll(rememberScrollState()),
                ) {

                    CustomerNameTextField (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        name = state.customerName,
                        label = "Enter Name",
                        fontSize = 18,
                        icon = R.drawable.person_icon_light,
                        onValueChange = onCustomerNameChange,
                    )
                    PhoneNumberTextFieldWithLeadingIcon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        phoneNumber = state.phoneNumber,
                        label = "Phone Number",
                        onPhoneChanged = onPhoneChanged,
                        onCountryChanged = onCountryCodeChanged,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                        ),
                    )

                    CustomerNameTextField (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        name = state.amount.toCurrencyString(),
                        label = "Amount",
                        fontSize = 18,
                        icon = R.drawable.money_icon,
                        onValueChange = onCustomerNameChange,
                    )

//                    ReadOnlyAmountTextField(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 8.dp),
//                        amount = state.amount.toCurrencyString(),
//                    )

//                    Column {
//
//                        Text(
//                            text = "Amount",
//                            modifier.padding(start = 16.dp, top = 5.dp),
//                            fontSize = 13.sp,
//                            color = Color.Black,
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .border(
//                                    0.8.dp,
//                                    Color.Black,
//                                    RoundedCornerShape(5.dp),
//                                )
//                                .height(50.dp)
//                                .padding(5.dp),
//                        ) {
//
//
//                            Text(
//                                text = state.amount.toCurrencyString(),
//                                fontSize = 24.sp,
//                                color = Color.Black,
//                                modifier = Modifier
//                                    .padding(start = 15.dp)
//                                    .align(Alignment.CenterStart),
//                            )
//                        }
//
//                    }

                    BottomNextButton(
                        text = "Send Sms",
                        onClick = onSendSms,
                        enabled = state.formValid && !state.loading,
                        modifier = Modifier.padding(top = 15.dp),
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 8.dp, end = 14.dp, top = 2.dp, bottom = 1.dp),
                        text = "OR",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(600),
                            color = Color.Black,

                            ),
                    )

                    BottomNextButton(
                        text = "Generate QR Code",
                        onClick = onGenerateQrCode,
                        enabled = state.formValid && !state.loading,
                    )


                    if (state.loading) {
                        CircularProgressIndicator(
                            Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                        )
                    }
                }

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


@Suppress("ModifierMissing")
@Composable
fun ContactlessPaymentSuccessDialog(
    paymentResult: ContactlessPayment,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        content = {
            Surface(
                shape = RoundedCornerShape(5),
                color = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(id = R.drawable.contactless),
                            modifier = Modifier.size(32.dp),
                            contentDescription = "info",
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Contactless Payment",
                            style = MaterialTheme.typography.h6.copy(
                                fontSize = 24.sp,
                            ),
                            color = Color(0xFF053170),
                        )
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Scan QR code ${if (paymentResult.smsSent) "or check SMS" else ""}",
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        Card {


                            Image(
                                modifier = Modifier.height(240.dp),
                                painter = rememberAsyncImagePainter(paymentResult.qrCode.bitmap),
                                contentDescription = "qr code",
                            )
                        }
                    }

                    BottomNextButton(
                        text = "Continue",
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 15.dp),

                        )
                }
            }
        },
    )
}

@Composable
fun CustomerNameTextField(
    name: String,
    label:String,
    fontSize:Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon:Int,
) {

//    val colors = TextFieldDefaults.outlinedTextFieldColors(
//        focusedBorderColor = Color.Black, // Set the focused outline color
//        unfocusedBorderColor = Color.Black, // Set the unfocused outline color
//        cursorColor = Color.Black,
//        textColor = Color.Black,// Set the cursor color
//    )
//    OutlinedTextField(
//        modifier = modifier,
//        value = name,
//        onValueChange = onValueChange,
//        label = { Text(stringResource(R.string.enter_name), color = Color.Black) },
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(
//            imeAction = ImeAction.Next,
//            capitalization = KeyboardCapitalization.Words,
//            autoCorrect = false,
//        ),
//        colors = colors,
//    )

    Text(
        modifier = modifier,
        text = label,
        style = TextStyle(
            fontSize = 18.sp,
            color = Color.Black,
        ),
    )

    TextFieldWithUnderlineAndTrailingIcon(
        value = name,
        onValueChange = onValueChange,
        label = "Name",
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next,
            autoCorrect = false,
        ),
        fontSize = fontSize,
        iconResId = icon,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@NexgoN6Preview
@Composable
fun ContactlessScreenPreview() {
    LedgerGreenTheme {
        ContactlessScreen(
            state = ContactlessState(
                phoneNumber = PhoneNumber(
                    "12345678910",
                    CountryCodes.defaultUSPhoneCode,
                ),
                customerName = "Snoop Dog",
                amount = 59.99.toMoney(),
                sendSms = true,
                formValid = true,
                contactlessPayment = null,
                loading = false,
                error = null,
                onErrorShown = { },
                navigateNext = false,
            ),
            navigateToHome = { },
            onPhoneChanged = { },
            onCountryCodeChanged = { },
            onCustomerNameChange = { },
            onSendSms = { },
            onGenerateQrCode = { },
            onFinish = { },
            appBarConfig = DefaultAppBarConfig.preview,
            onNavigateToHome = {},
        )
    }
}

@NexgoN6Preview
@Composable
fun ContactlessPaymentSuccessDialogPreview() {
    LedgerGreenTheme {
        ContactlessPaymentSuccessDialog(
            paymentResult = ContactlessPayment(
                smsSent = true,
                qrCode = QrGenerator().generateForUrl("http://ledgergreen.com"),
            ),
            onDismiss = { },
        )
    }
}

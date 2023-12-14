package com.ledgergreen.terminal.ui.card.details

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.model.AmountDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.common.BackPressSealed
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.TextFieldWithUnderlineAndTrailingIcon
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBar
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.receipt.ReceiptState
import com.ledgergreen.terminal.ui.receipt.ReceiptViewModel
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.Title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun CardDetailsScreen(
    amount: String?,
    savedCardToken: String?,
    navigateToReceipt: () -> Unit,
    navigateToHome: () ->Unit,
    navigateToWallet: (customerId : String?) -> Unit,
    navigateAgreement: (amount : String?) -> Unit,
    navigateToCardReader: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardDetailsViewModel = hiltViewModel(),
    viewModel2: ReceiptViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    val state2 = viewModel2.state.collectAsState().value



    CardDetailsScreen(
        savedCardToken = savedCardToken,
        amountFromRemaining = amount!!,
        state = state,
        state2 = state2,
        onCardNumberChange = viewModel::onCardNumberChanged,
        onExpiryChange = viewModel::onExpiryDateChange,
        onCvvChange = viewModel::onCvvChange,
        onCardHolderChange = viewModel::onCardHolderNameChange,
        onZipChange = viewModel::onZipCodeChange,
        onProceed = viewModel::onProceed,
        onProceedWithTip = viewModel::onProceedWithTips,
        onNavigateNext = {
            viewModel.onNavigateConsumed()
            if (PageState.fromPage != "goods&service") {
                viewModel2.clearTransactionCache()
                navigateToWallet(state2.customerExtId.toString())
            }
            else
//                navigateAgreement(amount.toString())
                navigateToReceipt()
        },
        onOpenCardReader = trackClick(
            targetName = Clicks.rescanCard,
            onClick = navigateToCardReader,
        ),
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
        saveCardChecked = viewModel::saveCardChecked,
        onNavigateHome = {
            navigateToHome()
        }
    )
}
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
            System.exit(0)
        }
    }

    BackHandler(backPressState == BackPressSealed.Idle) {
        backPressState = BackPressSealed.InitialTouch
        showToast = true

    }
}



@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CardDetailsScreen(
    saveCardChecked: (checked:Boolean) -> Unit,
    savedCardToken: String?,
    amountFromRemaining : String,
    state: CardDetailsState,
    state2: ReceiptState,
    onCardNumberChange: (String) -> Unit,
    onExpiryChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onCardHolderChange: (String) -> Unit,
    onZipChange: (String) -> Unit,
    onProceed: (savedCardToken: String?) -> Unit,
    onProceedWithTip: (amount:Money,cardToken:String) -> Unit,
    onNavigateNext: () -> Unit,
    onOpenCardReader: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
    onNavigateHome:() ->Unit,
) {
//    if (state.navigateNext) {
//        LaunchedEffect("navi_card") {
//            onNavigateNext()
//        }
//    }

//    if (state.errorMsg.isNotEmpty()){
//        ErrorDialog(error = state.errorMsg) {
//
//        }
//    }
    if (state.errorMsg != null) {
        ErrorDialog(state.errorMsg, state.onErrorShown)
    }


    if (state2.receiptId.isNotEmpty() && PageState.fromPage!="goods&service") {
        PaymentSuccessfulDialog(
            from = "card",
            amount = state.amountDetails!!.order.toCurrencyString(),
            onPaymentDone = { onNavigateNext() },
            onReject = { true },
        )
    } else if (PageState.fromPage.equals("goods&service")) {
        if (state.navigateNext) {
            LaunchedEffect("navi_card") {
                onNavigateNext()
            }
        }
    }

    state.transactionError?.let {
        ErrorDialog(it, state.onErrorShown)
    }

    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        backgroundColor = Color.White,
        topBar = {
            DefaultAppBar(
                logoUrl = appBarConfig.logoUrl,
                onLogout = appBarConfig.onLogout,
                onSwitch = appBarConfig.onSwitch,
                buttonText = stringResource(R.string.switch_txt),
                actions = {
                    OutlinedButton(
                        onClick = onOpenCardReader,
                        modifier = Modifier.padding(end = 10.dp),
                        content = {
                            Icon(
                                modifier = Modifier
                                    .size(16.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.scan_card_icon),
                                contentDescription = "open card reader",
                                tint = Color(0xFFFF0043A5)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.scan_card), color = Color(0xFF06478D))
                        },
                    )
                },
                onNavigateToHome = {
                    onNavigateHome()
                },
            )
        },
    ) { paddingValues ->

        Box {
            Image(
                painter = painterResource(id = R.drawable.botton_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart)
            )

            BackHandler {
                Toast.makeText(context, "You cannot go back from this page", Toast.LENGTH_LONG)
                    .show()
            }

            var sheetShate =
                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

            var isChecked by remember { mutableStateOf(false) }
//        LaunchedEffect(sheetShate) {
//            sheetShate = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
//        }
            if (PageState.fromPage == "goods&service") {
                ModalBottomSheetLayout(
                    sheetState = sheetShate,
                    modifier = Modifier,
                    sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    sheetBackgroundColor = Color.White,
                    sheetContent =
                    {
                        Column(
                            Modifier
                                .padding(paddingValues)
                                .padding(bottom = 16.dp)
                                .height(270.dp),
                        ) {

                            Title(stringResource(R.string.accept_agreement), modifier = Modifier.padding(bottom = 10.dp,start = 16.dp, end = 16.dp, top = 16.dp))
                            Card(
                                Modifier
                                    .width(164.dp)
                                    .padding(start = 16.dp, end = 16.dp),
                                elevation = 5.dp,
                                border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                            ) {
                                Image(
                                    modifier = Modifier.padding(5.dp),
                                    painter = painterResource(R.drawable.west_bank_logo),
                                    contentDescription = "bank_logo"
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            ) {

                                if (state.loading)
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White
                                    )
                                Text(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(top = 8.dp, bottom = 8.dp),
                                    text = state.agreementText,
                                    style = MaterialTheme.typography.caption,
                                    color = Color.Black
                                )
                            }

                            BottomNextButton(
//                    text = "Add $${addDotAfterLastTwoDigits(amount)}",
                                text = stringResource(
                                    R.string.load,
                                    amountFromRemaining.toMoney()!!.toCurrencyString(),
                                ),
                                onClick = {
                                    onProceedWithTip(
                                        amountFromRemaining.toMoney()!!,
                                        savedCardToken!!
                                    )
                                },
                                enabled = if (!state.loading) {
                                    if (savedCardToken.equals("0"))
                                        state.proceedAvailable
                                    else
                                        true
                                } else false,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    },
                ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        if (savedCardToken!!.equals("0")) {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues),
                            ) {
                                Title(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = stringResource(R.string.enter_card_details),
                                )


                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                ) {
                                    Column(modifier = Modifier.weight(2f)) {
                                        CardNumberTextField(
                                            cardNumber = state.cardNumber,
                                            cardType = state.cardType,
                                            maxCardNumberLength = state.cardConstraints.cardNumberLength,
                                            onNumberChange = onCardNumberChange,
                                            isError = state.showWrongCardNumber,
                                        )
                                        Text(
                                            modifier = Modifier.alpha(state.showWrongCardNumber.toVisibility()),
                                            text = stringResource(R.string.error_wrong_card_number),
                                            style = MaterialTheme.typography.caption.copy(Color.Red),
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    ExpirationTextField(
                                        modifier = Modifier.weight(1f),
                                        expiration = state.expiryDate,
                                        onValueChange = onExpiryChange,
                                    )
                                }

                                Row(modifier = Modifier.padding(top = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,)
                                {
                                    CardHolderNameTextField(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .weight(1f),
                                        name = state.cardHolderName,
                                        onValueChange = onCardHolderChange,
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    CvvTextField(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .weight(1f),
                                        cvv = state.cvv,
                                        cvvMaxLength = state.cardConstraints.cvvLength,
                                        onValueChange = onCvvChange,
                                    )
                                }

                                var checked by remember{ mutableStateOf(false) }
                                val backgroundColor = if(checked) Color(0xFFFF0043A5) else Color.White
                                val cardColor = if(checked) Color.White else Color(0xFFFF0043A5)

                                Row(
                                    modifier = Modifier.padding(top = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ZipCodeTextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 16.dp),
                                        zip = state.zipCode,
                                        onValueChange = onZipChange,
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 22.dp, bottom = 13.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFFF0043A5),
                                                shape = RoundedCornerShape(5.dp)
                                            )
                                            .background(
                                                backgroundColor,
                                                shape = RoundedCornerShape(5.dp)
                                            )
                                            .size(45.dp)
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onTap = {
                                                        checked = !checked
                                                        saveCardChecked(checked)

                                                    },
                                                )
                                            },
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ){
                                        Image(painter = painterResource( id = if (checked) R.drawable.tick_icon_white else R.drawable.card_icon),
                                            contentDescription = null,
                                            Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = if (checked) "Card Saved" else "Save Card", fontSize = 16.sp, color = cardColor)

                                    }
                                }
                            }
                        }

                        if (state.amountDetails != null) {
                            state.amountDetails?.let { amountDetails ->
                                Box(modifier = Modifier) {
                                    AmountDetailsView(
                                        order = amountDetails.order,
                                        tip = amountDetails.tips,
                                        convenienceCharge = state.convenienceCharge,
                                    )

                                    CircularProgressIndicator(
                                        Modifier
                                            .align(Alignment.Center)
                                            .padding(bottom = 4.dp)
                                            .alpha(state.loading.toVisibility()),
                                    )
                                }
                            }
                        }

                        val scope = rememberCoroutineScope()
                        val keyboardController = LocalSoftwareKeyboardController.current
                        Row(
                            // continue
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(
                                    color = Color(0xFFFF0043A5),
                                    shape = RoundedCornerShape(size = 10.dp),
                                )
                                .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
                                .clickable {
                                    scope.launch {
                                        sheetShate.show()
                                        keyboardController?.hide()
                                    }
                                },
                        ) {

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                text = "Continue",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight(600),
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                ),
                            )

                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (savedCardToken!!.equals("0")) {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues),
                        ) {
                            Title(
                                modifier = Modifier.padding(top = 8.dp),
                                text = stringResource(R.string.enter_card_details),
                            )


                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                            ) {
                                Column(modifier = Modifier.weight(2f)) {
                                    CardNumberTextField(
                                        cardNumber = state.cardNumber,
                                        cardType = state.cardType,
                                        maxCardNumberLength = state.cardConstraints.cardNumberLength,
                                        onNumberChange = onCardNumberChange,
                                        isError = state.showWrongCardNumber,
                                    )
//                                    Text(
//                                        modifier = Modifier.alpha(state.showWrongCardNumber.toVisibility()),
//                                        text = stringResource(R.string.error_wrong_card_number),
//                                        style = MaterialTheme.typography.caption.copy(Color.Red),
//                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                ExpirationTextField(
                                    modifier = Modifier.weight(1f),
                                    expiration = state.expiryDate,
                                    onValueChange = onExpiryChange,
                                )
                            }

                            Row(modifier = Modifier.padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,)
                            {
                                CardHolderNameTextField(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .weight(1f),
                                    name = state.cardHolderName,
                                    onValueChange = onCardHolderChange,
                                )
                                Spacer(Modifier.width(16.dp))
                                CvvTextField(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .weight(1f),
                                    cvv = state.cvv,
                                    cvvMaxLength = state.cardConstraints.cvvLength,
                                    onValueChange = onCvvChange,
                                )
                            }

                            var checked by remember{ mutableStateOf(false) }
                            val backgroundColor = if(checked) Color(0xFFFF0043A5) else Color.White
                            val cardColor = if(checked) Color.White else Color(0xFFFF0043A5)

                            Row(
                                modifier = Modifier.padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ZipCodeTextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(top = 10.dp),
                                    zip = state.zipCode,
                                    onValueChange = onZipChange,
                                )
                                Spacer(Modifier.width(16.dp))
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
//                                        .height(45.dp)
                                        .padding(top = 22.dp, bottom = 13.dp)
                                        .background(
                                            backgroundColor,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFFF0043A5),
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .size(45.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    checked = !checked
                                                    saveCardChecked(checked)

                                                },
                                            )
                                        },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ){
                                    Image(painter = painterResource( id = if (checked) R.drawable.tick_icon_white else R.drawable.card_icon),
                                        contentDescription = null,
                                        Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = if (checked) "Card Saved" else "Save Card", fontSize = 16.sp, color = cardColor)

                                }
                            }
                        }
                    }



                    if (state.amountDetails != null) {
                        state.amountDetails?.let { amountDetails ->
                            Box(modifier = Modifier.padding(top = 16.dp)) {
                                AmountDetailsView(
                                    order = amountDetails.order,
                                    tip = amountDetails.tips,
                                    convenienceCharge = state.convenienceCharge,
                                )

                                CircularProgressIndicator(
                                    Modifier
                                        .align(Alignment.Center)
                                        .padding(bottom = 4.dp)
                                        .alpha(state.loading.toVisibility()),
                                )
                            }
                        }
                    }

                    BottomNextButton(
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                        onClick = { onProceed(savedCardToken) },
                        enabled = if (!state.loading) {
                            if (savedCardToken.equals("0"))
                                state.proceedAvailable
                            else
                                true
                        } else false,
                        text =
                            stringResource(
                                R.string.load,
                                amountFromRemaining.toMoney()!!.toCurrencyString()
                                    ?: "",
                            )
//                    text = "100"
                    )
                }
            }
        }
    }
}

@Composable
fun ZipCodeTextField(
    zip: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    TextFieldWithUnderlineAndTrailingIcon(
        value = zip,
        onValueChange = onValueChange,
        label = stringResource(R.string.billing_zip_code),
        iconResId = R.drawable.zip_icon,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        modifier = modifier)
//    LGTextField(
//        modifier = modifier,
//        value = zip,
//        onValueChange = { if (it.length <= 7) onValueChange(it) },
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Text,
//            imeAction = ImeAction.Done,
//        ),
//        leadingIcon = {
//            Box(
//                Modifier
//                    .width(14.dp)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.zip_icon),
//                    contentDescription = "zip",
//                    modifier = Modifier
//                        .width(25.dp)
//                        .align(Alignment.CenterEnd),
//                )
//            }
//        },
//        placeholder = { Text(stringResource(R.string.billing_zip_code), modifier = Modifier, color = Color.Black) },
//        label = { Text(stringResource(R.string.billing_zip_code), modifier = Modifier, color = Color.Black) },
//    )
}

@Composable
fun CvvTextField(
    cvv: String,
    cvvMaxLength: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {


    TextFieldWithUnderlineAndTrailingIcon(
        value = cvv,
        onValueChange = onValueChange,
        label = stringResource(R.string.security_code),
        iconResId = R.drawable.pin_icon,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        visualTransformation = PasswordVisualTransformation())

//    LGTextField(
//        modifier = modifier,
//        value = cvv,
//        onValueChange = {
//            if (it.length <= cvvMaxLength) onValueChange(it)
//        },
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.NumberPassword,
//            imeAction = ImeAction.Next,
//        ),
//        leadingIcon = {
//            Box(
//                Modifier
//                    .width(14.dp)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.pin_icon),
//                    contentDescription = "expiry",
//                    modifier = Modifier
//                        .width(25.dp)
//                        .align(Alignment.CenterEnd),
//                )
//            }
//        },
//        visualTransformation = PasswordVisualTransformation(),
//        label = { Text(stringResource(R.string.security_code), modifier = Modifier, color = Color.Black) },
//        placeholder = { Text(stringResource(R.string.security_code), modifier = Modifier, color = Color.Black) },
//    )
}

@Composable
fun AmountDetailsView(
    order: Money,
    tip: Money,
    convenienceCharge: Money?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Title(stringResource(R.string.amount_details), modifier = Modifier)
        AmountDetailsLine(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            title = stringResource(R.string.order_amount_colon),
            value = order,
        )

        if(!tip.toCurrencyString().contains("$0.00")) {

            AmountDetailsLine(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                title = stringResource(R.string.tip_amount_colon),
                value = tip,

                )
        }
        AmountDetailsLine(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.compliance_fee_colon),
            value = convenienceCharge,
//            value = convenienceCharge ?: order,
        )
    }
}

@Composable
fun AmountDetailsLine(
    title: String,
    value: Money?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.body1,
            color = Color(0xFF9EA0A2)
        )
        Text(
            text = value?.toCurrencyString() ?: "–",
            color = Color(0xFF9EA0A2),
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
fun CardHolderNameTextField(
    name: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    TextFieldWithUnderlineAndTrailingIcon(
        value = name,
        onValueChange = onValueChange,
        label = "",
        iconResId = R.drawable.person_icon_light,
        enabled = false,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
            imeAction = ImeAction.Next,
        ),
        modifier = modifier)

//    LGTextField(
//        modifier = modifier,
//        value = name,
//        onValueChange = onValueChange,
//        singleLine = true,
//        enabled = false,
//        keyboardOptions = KeyboardOptions(
//            capitalization = KeyboardCapitalization.Words,
//            autoCorrect = false,
//            imeAction = ImeAction.Next,
//        ),
//        leadingIcon = {
//            Box(
//                Modifier
//                    .width(14.dp)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.person_icon),
//                    contentDescription = "expiry",
//                    modifier = Modifier
//                        .width(25.dp)
//                        .align(Alignment.CenterEnd),
//                )
//            }
//        },
//        label = { Text(stringResource(R.string.card_holder_name), modifier = Modifier, color = Color.Black) },
//        placeholder = { Text(stringResource(R.string.card_holder_name), modifier = Modifier, color = Color.Black) },
//    )
}

@Composable
fun CardNumberTextField(
    cardNumber: String,
    maxCardNumberLength: Int,
    cardType: CardType,
    onNumberChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
            val iconRes = when (cardType) {
                CardType.VISA -> R.drawable.card_visa
                CardType.MASTERCARD -> R.drawable.card_mastercard
                CardType.AMERICAN_EXPRESS -> R.drawable.card_amex
                CardType.MAESTRO -> R.drawable.card_maestro
                CardType.DINNERS_CLUB -> R.drawable.card_diners
                CardType.DISCOVER -> R.drawable.card_discover
                CardType.JCB -> R.drawable.card_jcb
                else -> R.drawable.scan_card_icon
            }

    TextFieldWithUnderlineAndTrailingIcon(
        value = cardNumber,
        onValueChange = { if (it.length <= maxCardNumberLength) onNumberChange(it)},
        label = stringResource(R.string.card_number),
        isError = isError,
        iconResId = iconRes,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        visualTransformation = cardNumberMaskFromType(cardType))

//    LGTextField(
//        modifier = modifier,
//        value = cardNumber,
//        isError = isError,
//        visualTransformation = cardNumberMaskFromType(cardType),
//        singleLine = true,
//        onValueChange = {
//            if (it.length <= maxCardNumberLength) onNumberChange(it)
//        },
//        leadingIcon = {
//            val iconRes = when (cardType) {
//                CardType.VISA -> R.drawable.card_visa
//                CardType.MASTERCARD -> R.drawable.card_mastercard
//                CardType.AMERICAN_EXPRESS -> R.drawable.card_amex
//                CardType.MAESTRO -> R.drawable.card_maestro
//                CardType.DINNERS_CLUB -> R.drawable.card_diners
//                CardType.DISCOVER -> R.drawable.card_discover
//                CardType.JCB -> R.drawable.card_jcb
//                else -> null
//            }
//            Box(
//                Modifier
//                    .width(14.dp)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//            ) {
//                Image(
//                    painter = iconRes?.let { painterResource(iconRes) }
//                        ?: rememberVectorPainter(Icons.Default.CreditCard),
//                    contentDescription = "card type",
//                    modifier = Modifier
//                        .width(25.dp)
//                        .align(Alignment.CenterEnd),
//                )
//            }
//        },
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.NumberPassword,
//            imeAction = ImeAction.Next,
//        ),
//        placeholder = { Text(stringResource(R.string.card_number), modifier = Modifier, color = Color.Black) },
//        label = { Text(stringResource(R.string.card_number), modifier = Modifier, color = Color.Black) },
//    )
}

@Composable
fun ExpirationTextField(
    expiration: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    TextFieldWithUnderlineAndTrailingIcon(
        value = expiration,
        onValueChange = onValueChange,
        label = stringResource(R.string.expiration),
        iconResId = R.drawable.calendar_icon,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        visualTransformation = ExpirationDateMask(),
        modifier = modifier)


//    LGTextField(
//        modifier = modifier,
//        value = expiration,
//        visualTransformation = ExpirationDateMask(),
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.NumberPassword,
//            imeAction = ImeAction.Next,
//        ),
//        onValueChange = {
//            if (it.length <= 4) onValueChange(it)
//        },
//        textStyle = LocalTextStyle.current.copy(
//            textAlign = TextAlign.Center,
//        ),
//        leadingIcon = {
//            Box(
//                Modifier
//                    .width(14.dp)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.calendar_icon),
//                    contentDescription = "expiry",
//                    modifier = Modifier
//                        .width(25.dp)
//                        .align(Alignment.CenterEnd),
//                )
//            }
//        },
//        placeholder = { Text(stringResource(R.string.expiration),color = Color.Black) },
////        label = { Text(stringResource(R.string.expiration),color = Color.Black) },
//    )
}

@NexgoN6Preview
@Composable
fun CardDetailsScreenPreview() {
    LedgerGreenTheme {
        CardDetailsScreen(
            savedCardToken = "0",
            amountFromRemaining = "123",
            state = CardDetailsState(
                cardNumber = "0000111122223333",
                expiryDate = "1027",
                cardHolderName = "John Doe",
                cvv = "277",
                zipCode = "94-222",
//                amountDetails = null,
                amountDetails = AmountDetails(
                    order = 20.5.toMoney(),
                    tips = 3.0.toMoney(),
                ),
                convenienceCharge = 2.0.toMoney(),
                total = 25.5.toMoney(),
                navigateNext = false,
                cardType = CardType.MASTERCARD,
                cardConstraints = CardDetailsViewModel.defaultCardConstraints,
                onErrorShown = {},
                isValidCardNumber = false,
                cardFee = 0.0,
                amountPlusFee = null,
                agreementText = "Text",
                errorMsg = null,
                saveCard = false,
                ),

            state2 = ReceiptState(
                orderId = "LGO-1681301702332",
                location = "53a0f6d7",
//                terminal = "2956e3e5",
                totalAmount = "$24.5",
                amount = "$20.5",
//                tipAmount = "$2.5",
//                complianceFee = "$0.5",
                orderDate = Clock.System.now(),
//                cardNumber = "XXXX-XXXX-XXXX-1234",
//                cardType = "CREDIT",
                customerName = "Bruce Lee",
                accountNumber = "8131241774444211",
                customerPhoneNumber = "3104618288",
                receiptId = "",
                customerExtId = "12333333333333333",
                signatureUrl = """https://lg-test-bucket-1.s3.us-east-2.amazonaws.com/signature/
                    |cyrus-vimal-jack480174--1681300837911974.png""".trimMargin(),
                onNext = false,
                orderAmount = "10"
                , isLoading = false
            ),
            onCardNumberChange = {},
            onExpiryChange = {},
            onCvvChange = {},
            onCardHolderChange = {},
            onZipChange = {},
            onProceed = {},
            onProceedWithTip = {amount, cardToken ->  },
            onNavigateNext = {},
            onOpenCardReader = {},
            appBarConfig = DefaultAppBarConfig.preview,
            saveCardChecked = {},
            onNavigateHome = {}
        )
    }
}

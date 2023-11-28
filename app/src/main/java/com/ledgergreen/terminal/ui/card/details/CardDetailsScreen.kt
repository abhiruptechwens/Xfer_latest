package com.ledgergreen.terminal.ui.card.details

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.model.AmountDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.agreement.Agreement
import com.ledgergreen.terminal.ui.common.BackPressSealed
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.LGTextField
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBar
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.CustomerConfirmationDialog
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.receipt.ReceiptState
import com.ledgergreen.terminal.ui.receipt.ReceiptViewModel
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.Title
import com.ledgergreen.terminal.ui.tips.remainingAmount
import com.ledgergreen.terminal.ui.wallet.WalletState
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
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
        backgroundColor = Color(0xFF06478D),
        topBar = {
            DefaultAppBar(
                logoUrl = appBarConfig.logoUrl,
                onLogout = appBarConfig.onLogout,
                onSwitch = appBarConfig.onSwitch,
                buttonText = stringResource(R.string.switch_txt),
                actions = {
                    OutlinedButton(
                        onClick = onOpenCardReader,
                        content = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                imageVector = Icons.Default.Contactless,
                                contentDescription = "open card reader",
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.scan_card), color = Color(0xFF06478D))
                        },
                    )
                },
                onNavigateToHome = {
                    onNavigateHome()
                }
            )
        },
    ) { paddingValues ->

        BackHandler {
            Toast.makeText(context, "You cannot go back from this page", Toast.LENGTH_LONG).show()
        }

        var sheetShate =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

        var isChecked by remember { mutableStateOf(false) }
//        LaunchedEffect(sheetShate) {
//            sheetShate = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
//        }
        if(PageState.fromPage == "goods&service") {
            ModalBottomSheetLayout(
                sheetState = sheetShate,
                modifier = Modifier,
                sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
                sheetBackgroundColor = Color(0xFF06478D),
                sheetContent =
                {
                    Column(
                        Modifier
                            .padding(paddingValues)
                            .padding(16.dp),
                    ) {

                        Card(
                            Modifier
                                .width(164.dp),
                            elevation = 16.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                        ) {
                            Image(
                                modifier = Modifier.padding(5.dp),
                                painter = painterResource(R.drawable.west_bank_logo),
                                contentDescription = "bank_logo"
                            )
                        }

                        Title(stringResource(R.string.accept_agreement))

                        Box(modifier = Modifier
                            .height(100.dp)
                            .width(306.dp)
                            .padding(top = 8.dp)) {

                            if(state.loading)
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                            Text(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(top = 8.dp, bottom = 8.dp),
                                text = state.agreementText,
                                style = MaterialTheme.typography.caption,
                                color = Color.White
                            )
                        }

                        BottomNextButton(
//                    text = "Add $${addDotAfterLastTwoDigits(amount)}",
                            text = stringResource(
                                R.string.load,
                                amountFromRemaining.toMoney()!!.toCurrencyString(),
                            ),
                            onClick = { onProceedWithTip(amountFromRemaining.toMoney()!!,savedCardToken!!) },
                            enabled = if(!state.loading) {
                                if(savedCardToken.equals("0"))
                                state.proceedAvailable
                                else
                                    true
                            } else false,
                        )
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
                                .padding(paddingValues)
                                ,
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
                                Column(modifier = Modifier.weight(1f)) {
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
                                    modifier = Modifier.width(80.dp),
                                    expiration = state.expiryDate,
                                    onValueChange = onExpiryChange,
                                )
                            }

                            CardHolderNameTextField(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .fillMaxWidth(),
                                name = state.cardHolderName,
                                onValueChange = onCardHolderChange,
                            )

                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CvvTextField(
                                    modifier = Modifier.weight(1f),
                                    cvv = state.cvv,
                                    cvvMaxLength = state.cardConstraints.cvvLength,
                                    onValueChange = onCvvChange,
                                )
                                Spacer(Modifier.width(16.dp))
                                ZipCodeTextField(
                                    modifier = Modifier.weight(1f),
                                    zip = state.zipCode,
                                    onValueChange = onZipChange,
                                )
                            }
                        }

                        val checkboxColors = CheckboxDefaults.colors(
                            checkedColor = Color.White, // Color when checkbox is checked
                            uncheckedColor = Color.White, // Color when checkbox is unchecked
                            checkmarkColor = Color.White, // Color of the checkmark
                            disabledColor = Color.Gray, // Color when checkbox is disabled
                        )


                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.End)) {
                            Checkbox(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically),
                                colors = checkboxColors,
                                checked = state.saveCard,
                                onCheckedChange = {
                                    saveCardChecked(it)
                                }
                            )

                            Text(text = "Save Card",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterVertically))

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
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(size = 10.dp),
                            )
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                color = Color(0xE2FFFFFF),
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
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xE2083364),
                                textAlign = TextAlign.Center,
                            ),
                        )

                    }
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if(savedCardToken!!.equals("0")) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            ,
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
                            Column(modifier = Modifier.weight(1f)) {
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
                                modifier = Modifier.width(80.dp),
                                expiration = state.expiryDate,
                                onValueChange = onExpiryChange,
                            )
                        }

                        CardHolderNameTextField(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth(),
                            name = state.cardHolderName,
                            onValueChange = onCardHolderChange,
                        )

                        Row(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CvvTextField(
                                modifier = Modifier.weight(1f),
                                cvv = state.cvv,
                                cvvMaxLength = state.cardConstraints.cvvLength,
                                onValueChange = onCvvChange,
                            )
                            Spacer(Modifier.width(16.dp))
                            ZipCodeTextField(
                                modifier = Modifier.weight(1f),
                                zip = state.zipCode,
                                onValueChange = onZipChange,
                            )
                        }
                    }

                    val checkboxColors = CheckboxDefaults.colors(
                        checkedColor = Color.Transparent, // Color when checkbox is checked
                        uncheckedColor = Color.White, // Color when checkbox is unchecked
                        checkmarkColor = Color.Red, // Color of the checkmark
                        disabledColor = Color.Gray, // Color when checkbox is disabled
                    )


                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)) {
                        Checkbox(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            colors = checkboxColors,
                            checked = state.saveCard,
                            onCheckedChange = {
                                saveCardChecked(it)
                            }
                        )

                        Text(text = "Save Card",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterVertically))

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
                    enabled = if(!state.loading) {
                        if(savedCardToken.equals("0"))
                        state.proceedAvailable
                        else
                            true
                    } else false,
                    text = if (state.amountDetails != null) {
                        stringResource(
                            R.string.load,
                            amountFromRemaining.toMoney()!!.toCurrencyString()
                                ?: "",
                        )
                    } else "Add Card",
//                    text = "100"
                )
            }
        }
    }
}

@Composable
fun cardItem(index :Int, state: WalletState){

//    val iconRes = when (state.custResponse!!.card[index].card_type) {
//        CardType.VISA -> R.drawable.card_visa
//        CardType.MASTERCARD -> R.drawable.card_mastercard
//        CardType.AMERICAN_EXPRESS -> R.drawable.card_amex
//        CardType.MAESTRO -> R.drawable.card_maestro
//        CardType.DINNERS_CLUB -> R.drawable.card_diners
//        CardType.DISCOVER -> R.drawable.card_discover
//        CardType.JCB -> R.drawable.card_jcb
//        else -> null
//    }
    Column(
        modifier = Modifier
            .padding(start = 15.dp)
            .height(64.dp)
            .background(
                color = Color(0xFFF2F5F8),
                shape = RoundedCornerShape(size = 5.dp)
            )
            .padding(start = 10.dp, end = 20.dp)

    ) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(state.custResponse!!.card[index].image)
                .size(Size.ORIGINAL) // Set the target size to load the image at.
                .build()
        )

        Image(
            modifier = Modifier
                .width(70.dp)
                .height(45.dp)
                .padding(top = 10.dp),
            painter = painter
                ?: rememberVectorPainter(Icons.Default.CreditCard),
            contentDescription = "image description",
        )

        Text(
            text = state.custResponse.card[index].card_number,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )
        )
    }
}


@Composable
fun closeKeyboard(){
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus()
}

@Composable
fun ZipCodeTextField(
    zip: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LGTextField(
        modifier = modifier,
        value = zip,
        onValueChange = { if (it.length <= 7) onValueChange(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        label = { Text(stringResource(R.string.billing_zip_code), modifier = Modifier, color = Color.White) },
    )
}

@Composable
fun CvvTextField(
    cvv: String,
    cvvMaxLength: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LGTextField(
        modifier = modifier,
        value = cvv,
        onValueChange = {
            if (it.length <= cvvMaxLength) onValueChange(it)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        visualTransformation = PasswordVisualTransformation(),
        label = { Text(stringResource(R.string.security_code), modifier = Modifier, color = Color.White) },
    )
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
                    .padding(vertical = 8.dp),
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
            color = Color.White
        )
        Text(
            text = value?.toCurrencyString() ?: "–",
            color = Color.White,
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
    LGTextField(
        modifier = modifier,
        value = name,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = false,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
            imeAction = ImeAction.Next,
        ),
        label = { Text(stringResource(R.string.card_holder_name), modifier = Modifier, color = Color.White) },
    )
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
    LGTextField(
        modifier = modifier,
        value = cardNumber,
        isError = isError,
        visualTransformation = cardNumberMaskFromType(cardType),
        singleLine = true,
        onValueChange = {
            if (it.length <= maxCardNumberLength) onNumberChange(it)
        },
        leadingIcon = {
            val iconRes = when (cardType) {
                CardType.VISA -> R.drawable.card_visa
                CardType.MASTERCARD -> R.drawable.card_mastercard
                CardType.AMERICAN_EXPRESS -> R.drawable.card_amex
                CardType.MAESTRO -> R.drawable.card_maestro
                CardType.DINNERS_CLUB -> R.drawable.card_diners
                CardType.DISCOVER -> R.drawable.card_discover
                CardType.JCB -> R.drawable.card_jcb
                else -> null
            }
            Box(
                Modifier
                    .width(25.dp)
                    .fillMaxHeight(),
            ) {
                Image(
                    painter = iconRes?.let { painterResource(iconRes) }
                        ?: rememberVectorPainter(Icons.Default.CreditCard),
                    contentDescription = "card type",
                    modifier = Modifier
                        .width(25.dp)
                        .align(Alignment.Center),
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        label = { Text(stringResource(R.string.card_number), modifier = Modifier, color = Color.White) },
    )
}

@Composable
fun ExpirationTextField(
    expiration: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LGTextField(
        modifier = modifier,
        value = expiration,
        visualTransformation = ExpirationDateMask(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
        ),
        onValueChange = {
            if (it.length <= 4) onValueChange(it)
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
        ),
        label = { Text(stringResource(R.string.expiration),color = Color.White) },
    )
}

@NexgoN6Preview
@Composable
fun CardDetailsScreenPreview() {
    LedgerGreenTheme {
        CardDetailsScreen(
            savedCardToken = "",
            amountFromRemaining = "$123",
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

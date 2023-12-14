package com.ledgergreen.terminal.ui.tips

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.ui.common.CurrencyAmountInputVisualTransformation
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.LGTextField
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.SignatureView
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.round2Decimals
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.pin.bottomBorder
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.domain.TipOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

var remainingAmount = 0.0
var restAmount = 0.0
var hasTip = true

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TipsScreen(
    navigateToCardReader: (amount: String) -> Unit,
    navigateToWallet: (customerId: String?) -> Unit,
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    viewModel: TipsViewModel = hiltViewModel(),
) {

    hasTip = viewModel.hasTipsOptions()

    val state = viewModel.state.collectAsState().value


    TipsScreen(
        state = state,
        onProceedWithSignature = viewModel::onProceedWithSignature,
        onSigned = viewModel::onSigned,
        onProceed = viewModel::onProceed,
        clearError = {
            viewModel.clearError()
        },
        onProceedWithWalletBalance = viewModel::onProceedWithWalletBalance,
        onNavigateNext = {
            viewModel.onNavigationConsumed()
            if (state.tipCustom.isEmpty())
                AppState1.tips = selectedPercentageValue
            else
                AppState1.tips = state.tipCustom.toDouble()
            navigateToCardReader(restAmount.toString())
        },
        onNavigateNextToHomeScreen = {
            viewModel.onNavigationConsumed()
            navigateToWallet(state.customerExtId)
        },
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
        closeDialog = {
            viewModel.clearError()
        },
        navigateToHome = {
            navigateToHome()
        },

        )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TipsScreen(
    navigateToHome: () -> Unit,
    state: TipsState,
    onProceed: () -> Unit,
    onProceedWithSignature: () -> Unit,
    onProceedWithWalletBalance: (amount: Double, tips: Double) -> Unit,
    onSigned: (Signature?) -> Unit,
    onNavigateNext: () -> Unit,
    onNavigateNextToHomeScreen: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    clearError: () -> Unit,
    modifier: Modifier = Modifier,
    closeDialog: () -> Unit,
) {

    if (state.navigateNextToHomeScreen) {
        PaymentSuccessfulDialog(
            from = "goods&service",
            amount = state.orderAmountAfterTransaction,
            onPaymentDone = { onNavigateNextToHomeScreen() },
            onReject = { true },
        )
    }

    DisposableEffect(Unit) {
        // Reset selectedPercentageValue when navigating back
        onDispose {
            selectedPercentageValue = 0.0
        }
    }

    if (state.navigateNext) {
        LaunchedEffect("navi_order") {
            onNavigateNext()
        }
    }

    if (state.errorMsg != null && state.errorMsg != "Insufficient balance") {
        AlertDialog(
            modifier = Modifier.reportActivity(),
            onDismissRequest = { },
            title = {
                Row(verticalAlignment = CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colors.error,
                        contentDescription = "error",
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.failed),
                        style = MaterialTheme.typography.h6.copy(
                            color = MaterialTheme.colors.error,
                            fontSize = 24.sp,
                        ),
                    )
                }
            },
            text = {
                Text(
                    state.errorMsg,
                    style = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = clearError) {
                    Text(
                        stringResource(R.string.ok),
                    )
                }
            },
        )
    }

//    if (state.navigateNextToHomeScreen) {
//        LaunchedEffect("navi_order") {
//            onNavigateNextToHomeScreen()
//        }
//    }

    Scaffold(
        modifier = modifier,
        backgroundColor = Color.White,
        topBar = { SwitchAppBar(appBarConfig, navigateToHome, {}) },
    ) { paddingValues ->

        var showDialogBoolean by remember {
            mutableStateOf(false)
        }

        val sheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

        ModalBottomSheetLayout(
            sheetState = sheetState,
            modifier = Modifier,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetBackgroundColor = Color.White,
            sheetContent ={

                val totalAmount = if (state.tipCustom.isEmpty()) {
                    (state.orderAmount.drop(1).replace(",","").toDouble() + selectedPercentageValue).round2Decimals()
                } else {
                    (state.orderAmount.drop(1).replace(",","").toDouble() + state.tipCustom.toDouble()).round2Decimals()
//                selectedPercentageValue = 0.0
                }

                if (selectedPercentageValue != 0.00 || state.tipCustom.isNotEmpty()) {
                    AppState1.totalAmount = totalAmount.toMoney()
                } else
                    AppState1.totalAmount = state.orderAmount.drop(1).replace(",","").toDouble().toMoney()

                if (state.error != null && state.error.contains("Insufficient balance")) {
                    restAmount = totalAmount - state.walletBalance.toDouble()

                    callBottomBar(
                        amount = (((totalAmount) - state.walletBalance.toDouble()).round2Decimals()).toString(),
                        onClicked = {
                            onProceedWithSignature()
                            AppState1.balanceUpdate = ""
                        },
                    )
                }

            }
        ){
            Box {
                Image(
                    painter = painterResource(id = R.drawable.botton_lines),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.BottomStart),
                )

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
//                    stringResource(R.string.account_number).titleCase(),
                        "From my Xfer Account".titleCase(),
                        modifier = Modifier.padding(end = 10.dp),
                        fontSize = 14.sp,
                        color = Color(0xFFFF6DA9FF),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        state.accountNumber,
                        modifier = Modifier.padding(start = 5.dp),
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Right,
                    )
//                Spacer(Modifier.height(16.dp))
                }

//                Divider(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
//                        .height(0.75.dp) // You can adjust the height of the line
//                        .background(Color.Black),
//                )

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 20.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//
//                Text(
//                    stringResource(R.string.order_amount).titleCase(),
//                    modifier = Modifier.padding(start = 5.dp, end = 10.dp),
//                    fontSize = 14.sp,
//                    color = Color.LightGray,
//                    textAlign = TextAlign.Right,
//                )
//                Text(
//                    state.orderAmount,
//                    modifier = Modifier.padding(start = 5.dp),
//                    fontSize = 18.sp,
//                    color = Color.White,
//                    textAlign = TextAlign.Right,
//                )
////                Spacer(Modifier.height(16.dp))
//            }
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
//                    .height(0.75.dp) // You can adjust the height of the line
//                    .background(Color.White),
//            )

//            Text(
//                "Enter Account Number",
//                modifier = Modifier.padding(start = 15.dp, end = 10.dp),
//                fontSize = 14.sp,
//                color = Color.LightGray,
//                textAlign = TextAlign.Right,
//            )

//            Row(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(start = 8.dp, end = 14.dp, top = 8.dp)
//                    .border(
//                        width = 1.dp,
//                        color = Color(0xFF6DA9FF),
//                        shape = RoundedCornerShape(size = 5.dp),
//                    )
//                    .width(333.dp)
//                    .height(49.dp)
//                    .background(color = Color(0x66001F43), shape = RoundedCornerShape(size = 5.dp))
//                    .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
//                    .clickable { },
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    text = "12345678",
//                    style = TextStyle(
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight(400),
//                        color = Color(0xFFFFFFFF),
//
//                        ),
//                )
//            }


//            Title(stringResource(R.string.order_amount))

//            ReadOnlyAmountTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                amount = state.orderAmount,
//            )
                Spacer(Modifier.height(16.dp))

                if (hasTip) {
                    Column {

                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = "Enter Tip Amount",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(600),
                                color = Color.Black,

                                ),
                        )

                        Tips(
                            state = state,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            value = state.selectedTipOption,
                            amount = state.orderAmount.replace(",",""),
                            options = state.tipsOptions,
                            onTipPercentChange = {
                                state.eventSink(TipEvent.TipOptionSelected(it))
//                    val amt = state.orderAmount.drop(1).toDouble()
//                    total = amt + ((amt * it) / 100).round2Decimals()
                            },
                        )
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    Modifier
//                        .weight(1f)
//                        .height(1.dp)
//                        .background(MaterialTheme.colors.onBackground),
//                )
//                Text(
//                    modifier = Modifier.padding(horizontal = 16.dp),
//                    text = stringResource(R.string.or),
//                    style = MaterialTheme.typography.caption,
//                )
//                Box(
//                    Modifier
//                        .weight(1f)
//                        .height(1.dp)
//                        .background(MaterialTheme.colors.onBackground),
//                )
//            }

                        var text by rememberSaveable { mutableStateOf("") }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.height(48.dp),
                                horizontalArrangement = Arrangement.Center,
                            ) {

                                var isPlaceholderVisible by remember { mutableStateOf(true) }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.Bottom)
                                        .padding(top = 10.dp),
                                ) {
                                    val context = LocalContext.current
                                    var valueOk: Boolean
                                    var cursorPosition by remember { mutableStateOf(0) }
                                    var isFocused by remember { mutableStateOf(false) }

                                    BasicTextField(
                                        textStyle = LocalTextStyle.current.copy(
                                            color = Color(
                                                0xFFFF0043A5
                                            )
                                        ),
                                        cursorBrush = SolidColor(Color.White),
                                        value = text,
                                        onValueChange = {
                                            var enteredValue = ""
                                            if (it.count() { it == '.' } <= 1 && !it.contains(
                                                    Regex(
                                                        "[- ,]"
                                                    )
                                                )) {
//                                            it.replace(Regex("[-_]"),"")
                                                enteredValue = if (it.startsWith(".")) {
                                                    "0"
                                                } else it

                                                if (enteredValue.isNotEmpty()) {
                                                    if (enteredValue.toDouble() <= state.orderAmount.removePrefix(
                                                            "$",
                                                        ).replace(",","").toDouble()
                                                    ) {
                                                        text = enteredValue
                                                    } else {
                                                        showDialogBoolean = true
//                                                    showDialog("Tip cannot be more than order amount!")
//                                                    Toast.makeText(
//                                                        context,
//                                                        "Tip cannot be more than order amount!",
//                                                        Toast.LENGTH_LONG,
//                                                    ).show()
                                                        text = ""
                                                        enteredValue = ""
                                                    }
                                                } else text = ""
                                            } else text = ""
//                                        text = if (valueOk) it else "0"
                                            selectedPercentageValue = 0.0
                                            state.eventSink(TipEvent.CustomTipChanged(enteredValue))
                                            isPlaceholderVisible = enteredValue.isEmpty()
//                            if(text.isEmpty())
//                                text = "Enter Custom Tip Amount"
//                            isBackspaceButtonVisible = it.toString().isNotEmpty()
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done,
                                        ),
                                        singleLine = true,
                                        modifier = Modifier
                                            .height(IntrinsicSize.Min) // Ensure that the TextField matches the row height
                                            .padding(8.dp)
                                            .fillMaxSize(),


                                        )

                                    if (showDialogBoolean) {
                                        AlertDialog(
                                            modifier = Modifier.reportActivity(),
                                            onDismissRequest = { showDialogBoolean = false },
                                            text = {
                                                Text(
                                                    "Please tip less than the order amount.",
                                                    style = LocalTextStyle.current.copy(
                                                        fontSize = 20.sp,
                                                    ),
                                                )
                                            },
                                            confirmButton = {
                                                TextButton(onClick = {
                                                    showDialogBoolean = false
                                                }) {
                                                    Text(
                                                        stringResource(R.string.ok),
                                                    )
                                                }
                                            },
                                        )
                                    }

                                    if (isPlaceholderVisible) {
                                        Text(
                                            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                                            text = "Enter Custom Tip Amount",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color = Color(0xffFF6DA9FF),
                                            ),
                                        )
                                    }
                                }
//                    IconButton(
//                        modifier = Modifier
//                            .height(IntrinsicSize.Min) // Ensure that the TextField matches the row height
//                            .padding(8.dp),
//                        onClick = { text = text.dropLast(1)
//                                  isPlaceholderVisible = true},
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Backspace,
//                            contentDescription = "Backspace",
//                            modifier = Modifier
//                                .align(Alignment.Bottom),
//                            tint = Color.White,
//                        )
//                    }
                            }

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp, end = 5.dp)
                                    .height(0.75.dp) // You can adjust the height of the line
                                    .background(Color(0xffFF0043A5)),
                            )
                        }
                    }
                }

                Card(
                    Modifier
                        .height(150.dp)
                        .padding(start = 15.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                    elevation = 5.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                ) {
                    SignatureView(
                        onSigned = {
                            onSigned(it)
                        },
                    )
                }


//            CustomTipsTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                text = state.tipCustom,
//                label = { Text(stringResource(R.string.enter_custom_tip_amount)) },
//                onValueChange = { state.eventSink(TipEvent.CustomTipChanged(it)) },
//            )

//            Spacer(Modifier.weight(1f))

                val backgroundModifier = if (state.proceedAvailable) {
                    Modifier
                        .background(Color(0xFFFF0043A5), shape = RoundedCornerShape(5.dp))
                } else {
                    Modifier
                        .background(Color(0xFF706E6E), shape = RoundedCornerShape(5.dp))
                }


                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(100.dp)
                        .padding(top = 15.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(size = 5.dp),
                        )
                        .width(333.dp)
                        .height(80.dp)
                        .then(backgroundModifier)
                        .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
                        .clickable { },
                ) {

//                    val currencySymbolRegex = Regex("[\$€£¥₹₽₩₺₴₿﷼₱₭₦₢₸₥₠₫₣₧₨₮₯₪₣₢¢]")

//                    if (currencySymbolRegex.containsMatchIn(state.orderAmount)) {
                    if ((state.orderAmount.drop(1).replace(",","")
                            .toDouble() + selectedPercentageValue) > state.walletBalance.toDouble()
                    ) {
                        remainingAmount = state.orderAmount.drop(1).replace(",","")
                            .toDouble() + selectedPercentageValue - (state.walletBalance.toDouble())
                    }
//                    }

                    val sendingTips =
                        if (state.tipCustom.isEmpty() || state.tipCustom == "0" || state.tipCustom == "0.0") {
                            selectedPercentageValue
                        } else state.tipCustom.toDouble()

                    NextButton(
                        state = state,
                        onProceedAvailable = state.proceedAvailable,
                        name = state.accountNumber.toString(),
                        walletBalance = state.walletBalance.toDouble(),
                        amount = state.orderAmount.drop(1).replace(",","").toDouble(),
                        onProceedWithSignature = { onProceedWithSignature() },
                        onProceedWithWalletBalance = {
                            onProceedWithWalletBalance(
                                state.orderAmount.drop(
                                    1,
                                ).replace(",","").toDouble(),
                                sendingTips,
                            )
                        },
                        closeDialog = closeDialog,
                        sheetState = sheetState,
                    )

                }


//            Button(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(90.dp),
//                content = {
//                    Text(
//                        text = stringResource(R.string.sign),
//                        style = LocalTextStyle.current.copy(fontSize = 32.sp),
//                    )
//                },
//                onClick = onProceed,
//            )
            }
        }}

//        Box {
//            Image(painter = painterResource(id = R.drawable.botton_lines),
//                contentDescription = null,
//                modifier = Modifier.align(Alignment.BottomStart))
//
//            Column(
//                modifier = Modifier
//                    .padding(paddingValues)
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState()),
//            ) {
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 15.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//
//                    Text(
////                    stringResource(R.string.account_number).titleCase(),
//                        "From my Xfer Account".titleCase(),
//                        modifier = Modifier.padding(end = 10.dp),
//                        fontSize = 14.sp,
//                        color = Color(0xFFFF6DA9FF),
//                        textAlign = TextAlign.Right,
//                    )
//                    Text(
//                        state.accountNumber,
//                        modifier = Modifier.padding(start = 5.dp),
//                        fontSize = 15.sp,
//                        color = Color.Black,
//                        textAlign = TextAlign.Right,
//                    )
////                Spacer(Modifier.height(16.dp))
//                }
//
////                Divider(
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
////                        .height(0.75.dp) // You can adjust the height of the line
////                        .background(Color.Black),
////                )
//
////            Row(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(start = 20.dp),
////                verticalAlignment = Alignment.CenterVertically,
////            ) {
////
////                Text(
////                    stringResource(R.string.order_amount).titleCase(),
////                    modifier = Modifier.padding(start = 5.dp, end = 10.dp),
////                    fontSize = 14.sp,
////                    color = Color.LightGray,
////                    textAlign = TextAlign.Right,
////                )
////                Text(
////                    state.orderAmount,
////                    modifier = Modifier.padding(start = 5.dp),
////                    fontSize = 18.sp,
////                    color = Color.White,
////                    textAlign = TextAlign.Right,
////                )
//////                Spacer(Modifier.height(16.dp))
////            }
////            Divider(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
////                    .height(0.75.dp) // You can adjust the height of the line
////                    .background(Color.White),
////            )
//
////            Text(
////                "Enter Account Number",
////                modifier = Modifier.padding(start = 15.dp, end = 10.dp),
////                fontSize = 14.sp,
////                color = Color.LightGray,
////                textAlign = TextAlign.Right,
////            )
//
////            Row(
////                modifier = Modifier
////                    .align(Alignment.CenterHorizontally)
////                    .padding(start = 8.dp, end = 14.dp, top = 8.dp)
////                    .border(
////                        width = 1.dp,
////                        color = Color(0xFF6DA9FF),
////                        shape = RoundedCornerShape(size = 5.dp),
////                    )
////                    .width(333.dp)
////                    .height(49.dp)
////                    .background(color = Color(0x66001F43), shape = RoundedCornerShape(size = 5.dp))
////                    .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
////                    .clickable { },
////                verticalAlignment = Alignment.CenterVertically,
////            ) {
////                Text(
////                    text = "12345678",
////                    style = TextStyle(
////                        fontSize = 15.sp,
////                        fontWeight = FontWeight(400),
////                        color = Color(0xFFFFFFFF),
////
////                        ),
////                )
////            }
//
//
////            Title(stringResource(R.string.order_amount))
//
////            ReadOnlyAmountTextField(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(top = 8.dp),
////                amount = state.orderAmount,
////            )
//                Spacer(Modifier.height(16.dp))
//
//                if (hasTip) {
//                    Column {
//
//                        Text(
//                            modifier = Modifier.padding(start = 15.dp),
//                            text = "Enter Tip Amount",
//                            style = TextStyle(
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight(600),
//                                color = Color.Black,
//
//                                ),
//                        )
//
//                        Tips(
//                            state = state,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 8.dp),
//                            value = state.selectedTipOption,
//                            amount = state.orderAmount.replace(",",""),
//                            options = state.tipsOptions,
//                            onTipPercentChange = {
//                                state.eventSink(TipEvent.TipOptionSelected(it))
////                    val amt = state.orderAmount.drop(1).toDouble()
////                    total = amt + ((amt * it) / 100).round2Decimals()
//                            },
//                        )
//                        Spacer(Modifier.height(8.dp))
////            Row(verticalAlignment = Alignment.CenterVertically) {
////                Box(
////                    Modifier
////                        .weight(1f)
////                        .height(1.dp)
////                        .background(MaterialTheme.colors.onBackground),
////                )
////                Text(
////                    modifier = Modifier.padding(horizontal = 16.dp),
////                    text = stringResource(R.string.or),
////                    style = MaterialTheme.typography.caption,
////                )
////                Box(
////                    Modifier
////                        .weight(1f)
////                        .height(1.dp)
////                        .background(MaterialTheme.colors.onBackground),
////                )
////            }
//                        Spacer(Modifier.height(8.dp))
//
//                        var text by rememberSaveable { mutableStateOf("") }
//
//                        Column(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp),
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                        ) {
//                            Row(
//                                modifier = Modifier.height(48.dp),
//                                horizontalArrangement = Arrangement.Center,
//                            ) {
//
//                                var isPlaceholderVisible by remember { mutableStateOf(true) }
//                                Box(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .align(Alignment.Bottom)
//                                        .padding(top = 10.dp),
//                                ) {
//                                    val context = LocalContext.current
//                                    var valueOk: Boolean
//                                    var cursorPosition by remember { mutableStateOf(0) }
//                                    var isFocused by remember { mutableStateOf(false) }
//
//                                    BasicTextField(
//                                        textStyle = LocalTextStyle.current.copy(
//                                            color = Color(
//                                                0xFFFF0043A5
//                                            )
//                                        ),
//                                        cursorBrush = SolidColor(Color.White),
//                                        value = text,
//                                        onValueChange = {
//                                            var enteredValue = ""
//                                            if (it.count() { it == '.' } <= 1 && !it.contains(
//                                                    Regex(
//                                                        "[- ,]"
//                                                    )
//                                                )) {
////                                            it.replace(Regex("[-_]"),"")
//                                                enteredValue = if (it.startsWith(".")) {
//                                                    "0"
//                                                } else it
//
//                                                if (enteredValue.isNotEmpty()) {
//                                                    if (enteredValue.toDouble() <= state.orderAmount.removePrefix(
//                                                            "$",
//                                                        ).replace(",","").toDouble()
//                                                    ) {
//                                                        text = enteredValue
//                                                    } else {
//                                                        showDialogBoolean = true
////                                                    showDialog("Tip cannot be more than order amount!")
////                                                    Toast.makeText(
////                                                        context,
////                                                        "Tip cannot be more than order amount!",
////                                                        Toast.LENGTH_LONG,
////                                                    ).show()
//                                                        text = ""
//                                                        enteredValue = ""
//                                                    }
//                                                } else text = ""
//                                            } else text = ""
////                                        text = if (valueOk) it else "0"
//                                            selectedPercentageValue = 0.0
//                                            state.eventSink(TipEvent.CustomTipChanged(enteredValue))
//                                            isPlaceholderVisible = enteredValue.isEmpty()
////                            if(text.isEmpty())
////                                text = "Enter Custom Tip Amount"
////                            isBackspaceButtonVisible = it.toString().isNotEmpty()
//                                        },
//                                        keyboardOptions = KeyboardOptions(
//                                            keyboardType = KeyboardType.Number,
//                                            imeAction = ImeAction.Done,
//                                        ),
//                                        singleLine = true,
//                                        modifier = Modifier
//                                            .height(IntrinsicSize.Min) // Ensure that the TextField matches the row height
//                                            .padding(8.dp)
//                                            .fillMaxSize(),
//
//
//                                        )
//
//                                    if (showDialogBoolean) {
//                                        AlertDialog(
//                                            modifier = Modifier.reportActivity(),
//                                            onDismissRequest = { showDialogBoolean = false },
//                                            text = {
//                                                Text(
//                                                    "Please tip less than the order amount.",
//                                                    style = LocalTextStyle.current.copy(
//                                                        fontSize = 20.sp,
//                                                    ),
//                                                )
//                                            },
//                                            confirmButton = {
//                                                TextButton(onClick = {
//                                                    showDialogBoolean = false
//                                                }) {
//                                                    Text(
//                                                        stringResource(R.string.ok),
//                                                    )
//                                                }
//                                            },
//                                        )
//                                    }
//
//                                    if (isPlaceholderVisible) {
//                                        Text(
//                                            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
//                                            text = "Enter Custom Tip Amount",
//                                            style = TextStyle(
//                                                fontSize = 14.sp,
//                                                color = Color(0xffFF6DA9FF),
//                                            ),
//                                        )
//                                    }
//                                }
////                    IconButton(
////                        modifier = Modifier
////                            .height(IntrinsicSize.Min) // Ensure that the TextField matches the row height
////                            .padding(8.dp),
////                        onClick = { text = text.dropLast(1)
////                                  isPlaceholderVisible = true},
////                    ) {
////                        Icon(
////                            imageVector = Icons.Default.Backspace,
////                            contentDescription = "Backspace",
////                            modifier = Modifier
////                                .align(Alignment.Bottom),
////                            tint = Color.White,
////                        )
////                    }
//                            }
//
//                            Divider(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 5.dp, end = 5.dp)
//                                    .height(0.75.dp) // You can adjust the height of the line
//                                    .background(Color(0xffFF0043A5)),
//                            )
//                        }
//                    }
//                }
//
//                Card(
//                    Modifier
//                        .height(120.dp)
//                        .padding(start = 15.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
//                    elevation = 5.dp,
//                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
//                ) {
//                    SignatureView(
//                        onSigned = {
//                            onSigned(it)
//                        },
//                    )
//                }
//
//
////            CustomTipsTextField(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(bottom = 16.dp),
////                text = state.tipCustom,
////                label = { Text(stringResource(R.string.enter_custom_tip_amount)) },
////                onValueChange = { state.eventSink(TipEvent.CustomTipChanged(it)) },
////            )
//
////            Spacer(Modifier.weight(1f))
//
//                val backgroundModifier = if (state.proceedAvailable) {
//                    Modifier
//                        .background(Color(0xFFFF0043A5), shape = RoundedCornerShape(5.dp))
//                } else {
//                    Modifier
//                        .background(Color(0xFF706E6E), shape = RoundedCornerShape(5.dp))
//                }
//
//
//                Row(
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .height(80.dp)
//                        .padding(top = 15.dp)
//                        .border(
//                            width = 1.dp,
//                            color = Color.White,
//                            shape = RoundedCornerShape(size = 5.dp),
//                        )
//                        .width(333.dp)
//                        .height(60.dp)
//                        .then(backgroundModifier)
//                        .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
//                        .clickable { },
//                ) {
//
////                    val currencySymbolRegex = Regex("[\$€£¥₹₽₩₺₴₿﷼₱₭₦₢₸₥₠₫₣₧₨₮₯₪₣₢¢]")
//
////                    if (currencySymbolRegex.containsMatchIn(state.orderAmount)) {
//                        if ((state.orderAmount.drop(1).replace(",","")
//                                .toDouble() + selectedPercentageValue) > state.walletBalance.toDouble()
//                        ) {
//                            remainingAmount = state.orderAmount.drop(1).replace(",","")
//                                .toDouble() + selectedPercentageValue - (state.walletBalance.toDouble())
//                        }
////                    }
//
//                    val sendingTips =
//                        if (state.tipCustom.isEmpty() || state.tipCustom == "0" || state.tipCustom == "0.0") {
//                            selectedPercentageValue
//                        } else state.tipCustom.toDouble()
//
//                    NextButton(
//                        state = state,
//                        onProceedAvailable = state.proceedAvailable,
//                        name = state.accountNumber.toString(),
//                        walletBalance = state.walletBalance.toDouble(),
//                        amount = state.orderAmount.drop(1).replace(",","").toDouble(),
//                        onProceedWithSignature = { onProceedWithSignature() },
//                        onProceedWithWalletBalance = {
//                            onProceedWithWalletBalance(
//                                state.orderAmount.drop(
//                                    1,
//                                ).replace(",","").toDouble(),
//                                sendingTips,
//                            )
//                        },
//                        closeDialog = closeDialog,
//                    )
//
//                }
//
//
////            Button(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .height(90.dp),
////                content = {
////                    Text(
////                        text = stringResource(R.string.sign),
////                        style = LocalTextStyle.current.copy(fontSize = 32.sp),
////                    )
////                },
////                onClick = onProceed,
////            )
//            }
//        }
    }
}
@Composable
fun showDialog(errorText: String){
    ErrorDialog(error = errorText,{true})
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NextButton(
    state: TipsState,
    onProceedAvailable: Boolean,
    name: String,
    walletBalance: Double,
    amount: Double,
    onProceedWithWalletBalance: () -> Unit,
    onProceedWithSignature: () -> Unit,
    closeDialog: () -> Unit,
    sheetState: ModalBottomSheetState,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(100.dp),
    ) {

        val scope = rememberCoroutineScope()
        var clicked by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .height(100.dp)
                .align(CenterHorizontally)
                .clickable {
                    if (onProceedAvailable) {
                        if (walletBalance > (amount + selectedPercentageValue)) {
                            onProceedWithWalletBalance()
                            if (state.error != null && state.error.contains("Insufficient balance")) {

                                scope.launch {
                                    sheetState.show()
                                }
                            }

                        } else {
//                            clicked = true
                            onProceedWithWalletBalance()
                            if (state.error != null && state.error.contains("Insufficient balance")) {

                                scope.launch {
                                    sheetState.show()
                                }
                            }
//                        onProceed()
                        }
                    }


                },
        ) {

            val totalAmount = if (state.tipCustom.isEmpty()) {
                (amount + selectedPercentageValue).round2Decimals()
            } else {
                (amount + state.tipCustom.toDouble()).round2Decimals()
//                selectedPercentageValue = 0.0
            }

            if (state.error != null && state.error.contains("Insufficient balance")) {

                scope.launch {
                    sheetState.show()
                }
            }
//
//            if (selectedPercentageValue != 0.00 || state.tipCustom.isNotEmpty()) {
//                AppState1.totalAmount = totalAmount.toMoney()
//            } else
//                AppState1.totalAmount = amount.toMoney()
//
//            if (state.error != null && state.error.contains("Insufficient balance")) {
//                restAmount = totalAmount - walletBalance


//                callBottomBar(
//                    amount = (((totalAmount) - walletBalance).round2Decimals()).toString(),
//                    onClicked = {
//                        onProceedWithSignature()
//                        AppState1.balanceUpdate = ""
//                    },
//                )
//                NoBalanceWalletDialog(
//                    name = name,
//                    amount = (((totalAmount) - walletBalance).round2Decimals()).toString(),
//                    onClicked = {
//                        onProceedWithSignature()
//                        AppState1.balanceUpdate = ""
//                    },
//                    onReject = { closeDialog() },
//                )
//            }

            Image(
//            modifier = Modifier.padding(start = 120.dp, end = 10.dp),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.xfericon2),
                contentDescription = "Icon",
            )
            Text(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(CenterVertically),
                text = totalAmount.toMoney().toCurrencyString(),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight(600),
                    color = Color.White,

                    ),
            )
        }
    }
}

@Composable
fun callBottomBar(
    amount: String,
    onClicked: () -> Unit,
) {

    Box(modifier = Modifier.height(300.dp)) {

//        Icon(modifier = Modifier
//            .align(Alignment.TopEnd)
//            .clickable { onReject() },
//            painter = painterResource(id = R.drawable.close), contentDescription = null)

        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {

            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.no_balance),
                contentDescription = null,
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                text = "Load Funds to Proceed.",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                ),
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp),
                onClick = onClicked,
                colors = ButtonDefaults.buttonColors(Color(0xFFFF0043A5)),
                shape = RoundedCornerShape(5.dp),
                content = {
                    Text(
                        "Load ${amount.toMoney()!!.toCurrencyString()}",
                        color = Color.White,
                        style = MaterialTheme.typography.h6,
                    )
                },
            )
        }
    }

}

@Composable
fun ReadOnlyAmountTextField(
    amount: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        modifier = modifier.bottomBorder(1.dp, MaterialTheme.colors.onBackground),
        value = amount,
        singleLine = true,
        onValueChange = {},
        readOnly = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
            color = Color.White,
        ),
    )
}

@Composable
private fun CustomTipsTextField(
    text: String,
    label: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LGTextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            val value = if (it.startsWith("0")) {
                ""
            } else {
                it
            }
            onValueChange(value)
        },
        label = label,
        visualTransformation = CurrencyAmountInputVisualTransformation(
            fixedCursorAtTheEnd = true,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        ),
    )
}

var selectedPercentageValue = 0.0
var percentageValue = 0.0

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Tips(
    state: TipsState,
    value: TipOption?,
    options: ImmutableList<TipOption>,
    amount: String,
    onTipPercentChange: (TipOption) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier.padding(start = 15.dp,end = 15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        options.forEach { option ->
            val selected = value == option
            Card(
                modifier = Modifier.size(100.dp, 70.dp),
                backgroundColor = if (!selected)
                    Color.White
                else Color(0xFFFF0043A5),
                border = if(!selected)
                    BorderStroke(1.dp, color = Color(0xFFFF0043A5)) else BorderStroke(1.dp, color = Color.White),
                onClick = {
                    onTipPercentChange(option)
                    if (option is TipOption.PercentageTipOption) {
                        selectedPercentageValue =
                            (amount.drop(1).toDouble() * option.percentage) / 100
                    } else if (option is TipOption.FlatTipOption) {
                        selectedPercentageValue = option.amount.toDouble()
                    }
                },
            ) {
                Box(Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = when (option) {
                                is TipOption.FlatTipOption -> option.amount.toCurrencyString()
                                is TipOption.PercentageTipOption -> "${option.percentage}%"
                            },
                            fontSize = 24.sp,
                            color = if (!selected) Color.Black else Color.White,
                        )

                        var underTextShow: Boolean = false
                        when (option) {
                            is TipOption.FlatTipOption -> underTextShow = false
                            is TipOption.PercentageTipOption -> underTextShow = true
                        }

                        if (underTextShow && state.showTipAmount) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = when (option) {
                                    is TipOption.FlatTipOption -> {
                                        percentageValue = option.amount.toDouble()
                                        ""
//                                    if (state.showTipAmount)
//                                        option.amount.toCurrencyString()
//                                    else
//                                        ""
                                    }

                                    is TipOption.PercentageTipOption -> {
                                        percentageValue =
                                            (amount.drop(1).toDouble() * (option.percentage)) / 100
                                        percentageValue.toMoney().toCurrencyString()

                                    }
                                },
                                fontSize = 12.sp,
                                color = if (!selected) Color.Gray else Color(0xFFB3B0B0),
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.Black,
        style = MaterialTheme.typography.h6,
    )
}

@NexgoN6Preview
@Composable
fun OrderScreenPreview() {
    LedgerGreenTheme {
        TipsScreen(
            state = TipsState(
                orderAmount = "$1.44",
                selectedTipOption = TipOption.PercentageTipOption(15),
                tipsOptions = persistentListOf(
                    TipOption.PercentageTipOption(5),
                    TipOption.PercentageTipOption(10),
                    TipOption.PercentageTipOption(15),
//                    TipOption.FlatTipOption(20.0.toMoney()),
                ),
                tipCustom = "0",
                navigateNext = false,
                navigateNextToHomeScreen = false,
                eventSink = { },
                walletBalance = "123",
                accountNumber = "6254522455",
                agreementText = "Agreement text",
                agreementAccepted = true,
                signature = null,
                customerExtId = "",
                receiptId = "",
                orderAmountAfterTransaction = "123",
                error = null,
                errorMsg = null,
                showTipAmount = false,
            ),
            onProceed = {},
            onSigned = { },
            onProceedWithSignature = {},
            onProceedWithWalletBalance = { amount, tips -> },
            onNavigateNext = {},
            onNavigateNextToHomeScreen = {},
            appBarConfig = DefaultAppBarConfig.preview,
            clearError = {},
            closeDialog = {},
            navigateToHome = {},

            )
    }
}

@Composable
fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.reportActivity(),
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.error,
                    contentDescription = "error",
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.failed),
                    style = MaterialTheme.typography.h6.copy(
                        color = MaterialTheme.colors.error,
                        fontSize = 24.sp,
                    ),
                )
            }
        },
        text = {
            Text(
                error,
                style = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.ok),
                )
            }
        },
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
//    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(text = "IT IS a bottom Sheet")
            }
        },
        sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color.Gray.copy(alpha = 0.5f),
        sheetBackgroundColor = Color.White,
    ) {
        // Handle dismiss event, if needed
        DisposableEffect(Unit) {
            onDispose {
                onDismiss()
            }
        }
    }
}

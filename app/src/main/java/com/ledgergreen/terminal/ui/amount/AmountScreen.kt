package com.ledgergreen.terminal.ui.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.ui.common.AmountTextField
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PinPad
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.Title

@Composable
fun AmountScreen(
    contactless: Boolean,
    loadfunds: Boolean,
    navigateTips: () -> Unit,
    navigateAgreement: (amount:String) -> Unit,
    navigateToWallet: (customerId:String) -> Unit,
    navigateContactless: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AmountViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    AmountScreen(
        contactless = contactless,
        state = state,
        loadfunds = loadfunds,
        onAmountChange = viewModel::onAmountChange,
        onProceed = viewModel::onProceed,
        onNavigateToWallet = {
            navigateToWallet(AppState1.customerExtId)
            AppState1.balanceAmount = null
            AppState1.totalAmount = null
            AppState1.cardFee = null
            AppState1.amountPlusFee = null
            AppState1.cardType = ""
            AppState1.inputAmount = ""
            AppState1.cardDetails = null
            AppState1.tips = 0.0
            PageState.fromPage = ""
        },
        onNavigateNext = {
            // TODO: rework navigation
            viewModel.onNavigationHandled()
            AppState1.inputAmount = (state.amount.toDouble() / 100).toString()
            if (!contactless) {
                    // if store has tips options
                    if (!loadfunds)
                        navigateTips()
                    else
                        navigateAgreement((state.amount.toDouble() / 100).toString())
            } else {
                navigateContactless()
            }
        },
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
        navigateToHome = {
            navigateToHome()
        }
    )
}

@Composable
fun AmountScreen(
    contactless: Boolean,
    navigateToHome: () -> Unit,
    onNavigateToWallet:() -> Unit,
    loadfunds: Boolean,
    state: AmountState,
    onAmountChange: (String) -> Unit,
    onProceed: () -> Unit,
    onNavigateNext: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {
    if (state.navigateNext) {
        LaunchedEffect("navi_amount") {
            onNavigateNext()
        }
    }

    Scaffold(
        backgroundColor = Color.White,
        topBar = { SwitchAppBar(appBarConfig, navigateToHome, {}) },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        Box {
            Image(
                painter = painterResource(id = R.drawable.botton_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart)
            )
//        BackHandler {
//            onNavigateToWallet()
//        }
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(10.dp),
            ) {

                var text = ""
                if (loadfunds) {
                    text = "Load Amount"
                } else {
                    text = "Order Amount"
                    if (!contactless) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Column {
                                Text(
                                    stringResource(R.string.account_number).titleCase(),
                                    modifier = Modifier,
                                    fontSize = 18.sp,
                                    color = Color(0xFF6DA9FF),
                                    textAlign = TextAlign.Right,
                                )
                                Text(
                                    state.accNo!!,
                                    modifier = Modifier.padding(top = 5.dp),
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Right,
                                )
                            }


//                Spacer(Modifier.height(16.dp))
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 3.dp, bottom = 10.dp, start = 18.dp, end = 18.dp)
                                .height(0.75.dp) // You can adjust the height of the line
                                .background(Color.Black),
                        )
                    }

                }


                Text(
                    text = text.titleCase(),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    color = Color(0xFF6DA9FF),
                    fontSize = 18.sp
                )
//            Spacer(Modifier.height(4.dp))
//            Text(
//                stringResource(R.string.please_confirm_entering_the_total_amount),
//                style = MaterialTheme.typography.caption,
//                color = Color.White
//            )
                Spacer(Modifier.height(8.dp))
                AmountTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    value = state.amount,
                    onValueChange = onAmountChange,
                )
                Spacer(Modifier.height(8.dp))
                Spacer(Modifier.weight(1f))
                PinPad(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onAmountChange( if ((state.amount+it).toLong() > 200000) "" else state.amount + it)
                    },
                    onReset = {
                        onAmountChange("")
                    },
                    onBackspace = { onAmountChange(state.amount.dropLast(1)) },
                )
                BottomNextButton(
                    enabled = state.proceedAvailable,
                    onClick = onProceed,
                )
            }
        }
    }
}

@NexgoN6Preview
@Composable
fun AmountScreenPreview() {
    LedgerGreenTheme {
        AmountScreen(
            contactless = false,
            onNavigateToWallet = {},
            loadfunds = false,
            state = AmountState(
                amount = "1999",
                proceedAvailable = true,
                navigateNext = false,
                accNo = "1234566555"
            ),
            onNavigateNext = {},
            onProceed = {},
            onAmountChange = {},
            navigateToHome = {},
            appBarConfig = DefaultAppBarConfig.preview,
        )
    }
}

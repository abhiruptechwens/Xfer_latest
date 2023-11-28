package com.ledgergreen.terminal.ui.agreement

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.SignatureView
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.tips.Title

@Composable
fun AgreementScreen(
    amount: String?,
    navigateToCardReader: (amount:String) -> Unit,
    navigateToReceipt: () -> Unit,
    navigateHome: () ->Unit,
    modifier: Modifier = Modifier,
    viewModel: AgreementViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    var parts = listOf<String>()
    var from = ""
    var amountVal = ""
    amount?.let {
//            if (it.contains("-")) {
//                parts = it.split("-")
//                from = parts[0].toString()
//                amountVal = parts[1].toString()
//                AgreementScreen(
//                    amount = amountVal,
//                    from = from,
//                    state = state,
//                    onProceed = viewModel::onProceed,
//                    onProceedWithSavedSignature = viewModel::onProceedWithSavedSignature,
//                    onSigned = viewModel::onSigned,
//                    onNext = {
//                        viewModel.onNavigationConsume()
//                        navigateToCardReader(amountVal)
//                    },
//                    appBarConfig = defaultAppBarConfig(),
//                    modifier = modifier,
//                )
//            } else {
                AgreementScreen(
                    amount = amount.toMoney()!!.toCurrencyString(),
                    from = "",
                    state = state,
                    onProceed = viewModel::onProceed,
//                    onProceedWithSavedSignature = viewModel::onProceedWithSavedSignature,
                    onSigned = viewModel::onSigned,
                    onNext = {
                        viewModel.onNavigationConsume()
                        navigateToCardReader(amount)
                    },
                    appBarConfig = defaultAppBarConfig(),
                    modifier = modifier,
                    onNavigatHome = {
                        navigateHome()
                    }
                )
    }
}

@Composable
fun AgreementScreen(
    amount: String,
    from :String,
    state: AgreementState,
    onProceed: () -> Unit,
//    onProceedWithSavedSignature: (amount:String) -> Unit,
    onSigned: (Signature?) -> Unit,
    appBarConfig: DefaultAppBarConfig,
    onNext: () -> Unit,
    onNavigatHome : ()->Unit,
    modifier: Modifier = Modifier,
) {
    if (state.navigateNext) {
        LaunchedEffect("navi_agreement") {
            onNext()
        }
    }

    Scaffold(
        modifier = modifier,
        backgroundColor = Color(0xFF06478D),
        topBar = { SwitchAppBar(appBarConfig, onNavigatHome)},
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(16.dp),
        ) {

            Card(Modifier
                .width(164.dp),
                elevation = 16.dp,
                border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)) {
                Image(modifier = Modifier.padding(5.dp),painter = painterResource(R.drawable.west_bank_logo), contentDescription = "bank_logo")
            }

            Title(stringResource(R.string.accept_agreement))
            Agreement(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                text = state.agreementText,
            )
                Card(
                    Modifier
                        .height(150.dp)
                        .padding(vertical = 5.dp),
                    elevation = 16.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                ) {
                        SignatureView(
                            onSigned = onSigned,
                        )
                }

                BottomNextButton(
//                    text = "Add $${addDotAfterLastTwoDigits(amount)}",
                    text = "Add $amount",
                    onClick = onProceed,
                    enabled = state.proceedAvailable,
                )
        }
    }
}

fun addDotAfterLastTwoDigits(input: String): String {
    return if (input.length >= 2) {
        val lastTwoDigits = input.takeLast(2)
        val remainingDigits = input.dropLast(2)
        "$remainingDigits.$lastTwoDigits"
    } else {
        // Handle the case when the input string is less than 2 characters
        input
    }
}

@Composable
fun Agreement(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier
        .height(150.dp)
        .width(306.dp)) {
        Text(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, bottom = 8.dp),
            text = text,
            style = MaterialTheme.typography.caption,
            color = Color.White
        )
    }
}

@NexgoN6Preview
@Composable
fun AgreementScreenPreview() {
    LedgerGreenTheme {
        AgreementScreen(
            amount = "100",
            from = "",
            state = AgreementState(
                agreementText = "Agreement text",
                agreementAccepted = true,
                signature = null,
                navigateNext = false,
            ),
            onProceed = { },
//            onProceedWithSavedSignature = { },
            onSigned = { },
            onNext = { },
            onNavigatHome = { },
            appBarConfig = DefaultAppBarConfig.preview,
        )
    }
}

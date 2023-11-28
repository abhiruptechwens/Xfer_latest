package com.ledgergreen.terminal.ui.receipt

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.round2Decimals
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import java.time.format.DateTimeFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReceiptScreen(
    navigateToStart: () -> Unit,
    navigateToWallet: (customerId : String?) -> Unit,
    navigateToHome: () -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    var displayDialog by remember { mutableStateOf(false) }

        ReceiptScreen(
            displayDialog = displayDialog,
            state = state,
            onNext = {
                viewModel.onProceedWithWalletBalance(
                    AppState1.inputAmount.toDouble(),
                    AppState1.tips
                )
                displayDialog = true
//            viewModel.clearCache()

//                navigateToWallet(state.customerExtId.toString())
            },
            onNavigateNextToHomeScreen = {
                viewModel.onNavigationConsumed()
                navigateToStart()
            },
            navigateToHome = {
                navigateToHome()
            },
            appBarConfig = defaultAppBarConfig(),
        )
}

@Composable
fun ReceiptScreen(
    navigateToHome: () -> Unit,
    displayDialog: Boolean,
    state: ReceiptState,
    onNext: () -> Unit,
    onNavigateNextToHomeScreen: () ->Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {

    if (displayDialog){

        PaymentSuccessfulDialog(
            from="goods&service",
            amount = state.amount,
            onPaymentDone = { onNavigateNextToHomeScreen() },
            onReject = { true },
        )
    }
    val context = LocalContext.current

    BackHandler {
        Toast.makeText(context, "You cannot go back from this page", Toast.LENGTH_LONG).show()
    }


    Scaffold(modifier = modifier,
        backgroundColor = Color(0xFF06478D),
        topBar = { SwitchAppBar(appBarConfig, navigateToHome) },) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()){

            if (state.amount.isEmpty() && state.orderAmount.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {



                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        text = "Please click below to complete your transfer",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center

                        )
                    )

//                    val amtWithoutDol = if(state.amount.contains("$"))
//                        state.amount.drop(1)
//                    else
//                        state.amount.drop(1)

                    BottomNextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        onClick = onNext,
                        enabled = !state.isLoading,
                        text = "Transfer ${AppState1.totalAmount!!.toCurrencyString()}",
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 15.dp, top = 40.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF6DA9FF),
                                shape = RoundedCornerShape(size = 10.dp),
                            )
                            .background(
                                color = Color(0x96002E71),
                                shape = RoundedCornerShape(size = 10.dp)
                            ),
                    ) {

                        Text(
                            modifier = Modifier
                                .padding(14.dp, 9.dp, 14.dp, 0.dp)
                                .align(Alignment.CenterHorizontally),
                            text = "Your Wallet is loaded",
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight(400),
                                color = Color.White,
                                textAlign = TextAlign.Center

                            ),
                        )

                        Row(
                            // xfer icon button
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 15.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF6DA9FF),
                                    shape = RoundedCornerShape(size = 5.dp),
                                )
                                .height(60.dp)
                                .shadow(3.dp, RoundedCornerShape(size = 5.dp))
                                .background(
                                    color = Color(0xE2083364),
                                    shape = RoundedCornerShape(size = 5.dp)
                                )

                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                text = "Your Bank/Card Statement \n" +
                                    "will display as West Town Bank",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp

                            )

                        }


                    }

//                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "You will receive an SMS with your funding receipt to West Town Bank and can always view your history by clicking on activity ",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center

                        )
                    )
                }
            }
        }

    }
}

fun Instant.toDateString(): String {
    val localDatetime = toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMM d, uuuu")
    return formatter.format(localDatetime.toJavaLocalDateTime())
}

@NexgoN6Preview
@Composable
fun ReceiptScreenPreview() {
    LedgerGreenTheme {
        ReceiptScreen(
            navigateToHome = { },
            displayDialog = false,
            state = ReceiptState(
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
                receiptId = "12344556677878",
                accountNumber = "8131241774444211",
                customerPhoneNumber = "3104618288",
                customerExtId = "12344556778",
                signatureUrl = """https://lg-test-bucket-1.s3.us-east-2.amazonaws.com/signature/
                    |cyrus-vimal-jack480174--1681300837911974.png""".trimMargin(),
                onNext = false,
                orderAmount = "10",
                isLoading = false

            ),
            onNext = {},
            onNavigateNextToHomeScreen = {},
            appBarConfig = DefaultAppBarConfig.preview,
        )
    }
}

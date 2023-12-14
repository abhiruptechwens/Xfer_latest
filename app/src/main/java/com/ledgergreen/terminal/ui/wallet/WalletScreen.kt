package com.ledgergreen.terminal.ui.wallet

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.model.GetCustomer
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.card.details.CardType
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme


@Composable
fun WalletScreen(
    customerId: String,
    navigateToAmount: () -> Unit,
    navigateToAmountWithoutTip: () -> Unit,
    navigateToCardReader: () -> Unit,
    navigateToHome: () -> Unit,
//    viewModel: HomeViewModel = hiltViewModel(),
    viewModel: WalletScreenViewModel = hiltViewModel(),

    ){
    val composableKey = rememberUpdatedState("walletScreen")
    LaunchedEffect(composableKey.value) {
        // Make an API request when the screen is first displayed
        viewModel.onGetCustomer(customerId)
    }

    val state = viewModel.state.collectAsState().value

//    val state2 = viewModel1.state.collectAsState().value

    WalletScreen(
        navigateToHome = navigateToHome,
        customerId = customerId,
        state = state,
        onAmountWithoutTip = trackClick(
            targetName = Clicks.addNewButton,
            onClick = navigateToAmountWithoutTip,
        ),
        onAmount = trackClick(
            targetName = Clicks.addNewButton,
            onClick = navigateToAmount,
        ),
        onCardScreen = trackClick(
            targetName = Clicks.addNewButton,
            onClick = navigateToCardReader,
        ),
        appBarConfig = defaultAppBarConfig(),
    )
}


@Composable
fun WalletScreen(
    navigateToHome: () -> Unit,
    customerId: String,
    state: WalletState,
    onAmount: () -> Unit,
    onAmountWithoutTip: () -> Unit,
    onCardScreen: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {

    BackHandler {
        navigateToHome()
        PageState.fromPage = ""
    }


//    val darkBlue = 0xFF06478D
//    val lightBlue = "#005CE1".toColorInt()
//    val brush = linearGradient(
//        colors = listOf(Color(darkBlue),Color(lightBlue)),
//        start = Offset(0f, 0f),
//            end = Offset(0f, 100f),
//
//    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        backgroundColor = Color.White,
        topBar = { SwitchAppBar(appBarConfig, navigateToHome,{}) },
    ){
        paddingValues ->
        Log.i("WalletScreen", "WalletScreen: ${state.custResponse?.amount}")

        var isColumnVisible by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {

            Image(painter = painterResource(id = R.drawable.botton_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Center))
            } else {
                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {


//            Image(
//                modifier = Modifier
//                    .padding(15.dp, 0.dp, 0.dp, 0.dp)
//                    .width(63.dp)
//                    .height(41.dp),
//                painter = painterResource(id = R.drawable.xfericon),
//                contentDescription = "image description",
//                contentScale = ContentScale.None
//            )
                    Text(
                        modifier = Modifier.padding(15.dp, 20.dp, 0.dp, 10.dp),
                        color = Color(0xFFFF0043A5),
                        text = "Hi, ${state.custResponse?.name}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight(600),
                            color = Color.Black,

                            ),
                    )
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(15.dp)
//                    .height(100.dp)
//                    .background(color = Color(0xFF065FE1), shape = RoundedCornerShape(size = 10.dp))
//                    .clickable {
//
//                    }
//            ){
//
//                Text(
//                    modifier = Modifier
//                        .padding(14.dp, 9.dp, 0.dp, 0.dp)
//                        .align(Alignment.TopStart),
//                    text = "Available Balance is",
//                    style = TextStyle(
//                        fontSize = 17.sp,
//                        lineHeight = 22.sp,
//                        fontWeight = FontWeight(400),
//                        color = Color(0xFFFFFFFF),
//
//                        )
//                )
//
//                Text(
//                    modifier = Modifier
//                        .align(Alignment.BottomStart)
//                        .padding(
//                            14.dp, 10.dp, 0.dp, 14.dp
//                        ),
//                    text = "$100.00",
//                    style = TextStyle(
//                        fontSize = 35.sp,
//                        lineHeight = 22.sp,
//                        fontWeight = FontWeight(600),
//                        color = Color(0xFFFFFFFF),
//
//                        )
//                )
//                OutlinedButton(
//                    modifier = Modifier
//                        .padding(start = 8.dp, end = 14.dp)
//                        .align(Alignment.CenterEnd),
//                    onClick = onAmount,
//                ) {
//                    Text(
//                        "Load Funds",
//                        color = Color(0xFF000000),
//                    ) }
//
//
//            }

                    Card(modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(size = 5.dp)
                        ),
                        elevation = 4.dp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable {

                                },
                        ) {

                            Text(
                                modifier = Modifier
                                    .padding(14.dp, 9.dp, 0.dp, 0.dp)
                                    .align(Alignment.TopStart),
                                text = "Available Balance is",
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFFFF0043A5),

                                    ),
                            )

                            Text(
                                // funds view
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(
                                        14.dp, 10.dp, 0.dp, 10.dp,
                                    ),
                                text = "$${state.custResponse?.amount}",
//                    text = "$100",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight(600),
                                    color = Color((0xFFFF0043A5)),

                                    ),
                            )
                            OutlinedButton(
                                // load funds button
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 14.dp)
                                    .align(Alignment.CenterEnd),
                                onClick = onAmountWithoutTip,
                                border = BorderStroke(1.dp, color = Color((0xFFFF0043A5)))
                            ) {
                                Text(
                                    "Load Funds",
                                    color = Color((0xFFFF0043A5)),
                                )
                            }


                        }
                    }
                    if (!isColumnVisible) {

                        Box(modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 8.dp, end = 8.dp, top = 50.dp)) {

                            Row(
                                // xfer icon button
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(size = 10.dp),
                                    )
                                    .width(309.58.dp)
                                    .height(88.8.dp)
                                    .shadow(10.dp, RoundedCornerShape(size = 10.dp))
                                    .background(color = Color(0xff0043A5),
                                        shape = RoundedCornerShape(10.dp))
                                    .clickable { isColumnVisible = !isColumnVisible },
                            ) {

                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .align(Alignment.CenterVertically),
                                    painter = painterResource(id = R.drawable.xfericon2),
                                    contentDescription = "Icon"
                                )

                            }
                            Image(painter = painterResource(id = R.drawable.button_design),
                                contentDescription = null)
                        }
                    }


                    AnimatedVisibility(
                        // buttons that are hidden goods and service and friends and family
                        visible = isColumnVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {

                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxSize()
                        ) {

                            val context = LocalContext.current

                            Row(
                                // friends and family button
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(start = 16.dp, end = 16.dp, top = 25.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(size = 10.dp),
                                    )
                                    .shadow(10.dp, RoundedCornerShape(size = 10.dp))
                                    .width(290.dp)
                                    .height(80.dp)
                                    .background(
                                        color = Color(0xff0043A5),
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
                                    .clickable {

                                        Toast
                                            .makeText(context, "Coming Soon...", Toast.LENGTH_SHORT)
                                            .show()

                                    },
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterVertically),
                                    text = "Pay Friends or Family",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight(600),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                    ),
                                )

                            }

                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(start = 8.dp, end = 14.dp, top = 15.dp),
                                text = "OR",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(600),
                                    color = Color(0xFF061F5C),

                                    )
                            )

                            Row(
                                // goods and service button
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(start = 16.dp, end = 16.dp, top = 15.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .shadow(10.dp, RoundedCornerShape(size = 10.dp))
                                    .width(290.dp)
                                    .height(80.dp)
                                    .background(
                                        color = Color(0xff0043A5),
                                        shape = RoundedCornerShape(size = 10.dp)
                                    )
                                    .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
                                    .clickable {
                                        onAmount()
                                        state.setPage("goods&service")
                                        PageState.fromPage = "goods&service"
                                    },
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterVertically),
                                    text = "Pay for Goods & Services",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight(600),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                    )
                                )

                            }
                        }
                    }

//                    Text(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 10.dp, start = 15.dp),
//                        text = "Funding Methods",
//                        style = TextStyle(
//                            fontSize = 15.sp,
//                            lineHeight = 22.sp,
//                            fontWeight = FontWeight(400),
//                            color = Color(0xFFFFFFFF),
//
//                            )
//                    )

                    // saved card ui
//                    LazyRow(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
//                        userScrollEnabled = true
//                    ) {
//                        state.custResponse?.card?.size?.let {
//                            items(it) { items ->
//                                cardItem(index = items, state = state)
//                            }
//                        }
//                    }

//                    Row(
//                        // add new card button
//                        modifier = Modifier
//                            .align(Alignment.CenterHorizontally)
//                            .padding(start = 8.dp, end = 14.dp, top = 18.dp)
//                            .border(
//                                width = 1.dp,
//                                color = Color(0xFF6DA9FF),
//                                shape = RoundedCornerShape(size = 20.dp)
//                            )
//                            .width(333.dp)
//                            .height(60.dp)
//                            .background(
//                                color = Color(0xE2083364),
//                                shape = RoundedCornerShape(size = 20.dp)
//                            )
//                            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
//                            .clickable { onCardScreen() },
//                    ) {
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .align(Alignment.CenterVertically),
//                            text = "Add new card +",
//                            style = TextStyle(
//                                fontSize = 14.sp,
//                                lineHeight = 22.sp,
//                                fontWeight = FontWeight(600),
//                                color = Color(0xFFFFFFFF),
//                                textAlign = TextAlign.Center,
//                            )
//                        )
//
//                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        // terms and conditions
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp),
                        text = stringResource(id = R.string.xfer_powered_by_west_town_bank_trust),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(400),
                            color = Color.Black,

                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                        ),
                    )

                }

            }
        }

    }

}
//@Composable
//fun cardItem(index :Int, state: WalletState){
//
//    Column(
//        modifier = Modifier
//            .padding(start = 15.dp)
//            .height(64.dp)
//            .background(
//                color = Color(0xFFF2F5F8),
//                shape = RoundedCornerShape(size = 5.dp)
//            )
//            .padding(start = 10.dp, end = 20.dp)
//
//    ) {
//
//        val painter = rememberAsyncImagePainter(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(state.custResponse!!.card[index].image)
//                .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
//                .build()
//        )
//
//        Image(
//            modifier = Modifier
//                .width(70.dp)
//                .height(45.dp)
//                .padding(top = 10.dp),
//            painter = painter
//                ?: rememberVectorPainter(Icons.Default.CreditCard),
////            painter = painterResource(id = R.drawable.card_visa),
//            contentDescription = "image description",
//        )
//
//        Text(
//            text = state.custResponse.card[index].card_number,
////            text = "**** 1234",
//            style = TextStyle(
//                fontSize = 14.sp,
//                fontWeight = FontWeight(400),
//                color = Color(0xFF000000),
//                textAlign = TextAlign.Center,
//            )
//        )
//    }
//}





@Preview(showBackground = true)
@Composable
fun WalletScreenPreview(){

    LedgerGreenTheme {
        WalletScreen(
            customerId = "",
            state = WalletState(
                customerId = "123",
                isLoading = false,
                error = null,
                custResponse = null,
                onErrorShown = { },
                setPage = { },
                appInfo = "123",
                success = true,
            ),
            navigateToHome = { },
            onAmount = { },
            onAmountWithoutTip = { },
            onCardScreen = { },
            appBarConfig = DefaultAppBarConfig.preview,

        )
    }
}


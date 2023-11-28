package com.ledgergreen.terminal.ui.card.reader

import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.card.dialog.CardDeleteDialog
import com.ledgergreen.terminal.ui.common.BottomNextButton
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun CardReaderScreen(
    amount: String?,
    navigateToCardDetails: (amount: String, cardToken: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardReaderViewModel = hiltViewModel(),
) {
    var state = viewModel.state.collectAsState().value

    AppState1.balanceAmount = amount!!.toMoney()
    CardReaderScreen(
        state = state,
        onStart = viewModel::onStart,
        onStop = viewModel::onStop,
        onEnterManually = trackClick(
            targetName = Clicks.enterCardManually,
            onClick = viewModel::onEnterManually,
        ),
        onSavedCardClicked = { cardToken ->
            if (cardToken != null) {
                navigateToCardDetails(amount, cardToken)
            }
        },
        onDeleteClicked = { cardToken, index ->
            if (cardToken != null) {
                viewModel.onDeletePressed(cardToken, index)
            }
        },
        onNavigateNext = { amount?.let { navigateToCardDetails(it, "0") } },
        modifier = modifier,
        dialogClose = {
            viewModel.onDialogClosed()
        }
    )
}

@Composable
fun CardReaderScreen(
    state: CardReaderState,
    dialogClose : () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onEnterManually: () -> Unit,
    onSavedCardClicked: (cardToken: String?) -> Unit,
    onDeleteClicked: (cardToken: String?, index: Int) -> Unit,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnStart by rememberUpdatedState(onStart)
    val currentOnStop by rememberUpdatedState(onStop)

    // If `lifecycleOwner` changes, dispose and reset the effect
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                currentOnStop()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (state.navigateNext) {
        LaunchedEffect(true) {
            state.onNavigateNextConsumed()
            onNavigateNext()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    state.message?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
            state.onMessageShown()
        }
    }

    val defaultBackgroundColor = Color(0xFF06478D)
    val errorBackgroundColor = MaterialTheme.colors.error

    val color = remember { Animatable(defaultBackgroundColor) }

    if (state.message != null) {
        LaunchedEffect(Unit) {
            // blinks background color 2 times
            val animTime = 150
            repeat(2) {
                color.animateTo(errorBackgroundColor, animationSpec = tween(animTime))
                color.animateTo(defaultBackgroundColor, animationSpec = tween(animTime))
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        backgroundColor = color.value,
    ) {

        if (state.success) {
            CardDeleteDialog(dialogClose)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.insert_chip_tap_or_swipe_card),
                color = Color.White,

                )
            Image(
                modifier = Modifier.width(220.dp),
                painter = painterResource(id = R.drawable.card_scanner_hint),
                contentDescription = "card scanner hint",
            )
            Spacer(Modifier.weight(1f))

            Box {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 4.dp)
                        .alpha(state.isLoading.toVisibility()),
                )
                if (!state.isLoading)
                    cardItems(
                        state = state,
                        onSavedCardClicked = onSavedCardClicked,
                        onDeleteClicked = onDeleteClicked,
                    )
            }

//            val context = LocalContext.current
            // saved card ui

//            Row(
//                // add new card button
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(start = 8.dp, end = 14.dp, top = 18.dp)
//                    .border(
//                        width = 1.dp,
//                        color = Color(0xFF6DA9FF),
//                        shape = RoundedCornerShape(size = 20.dp)
//                    )
//                    .width(333.dp)
//                    .height(40.dp)
//                    .background(
//                        color = Color(0xE2083364),
//                        shape = RoundedCornerShape(size = 20.dp)
//                    )
//                    .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
//                    .clickable { /*onCardScreen()*/ },
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterVertically),
//                    text = "Add new card +",
//                    style = TextStyle(
//                        fontSize = 14.sp,
//                        lineHeight = 22.sp,
//                        fontWeight = FontWeight(600),
//                        color = Color(0xFFFFFFFF),
//                        textAlign = TextAlign.Center,
//                    )
//                )
//
//            }

            BottomNextButton(
                text = stringResource(R.string.enter_manually),
                onClick = onEnterManually,
            )
        }
    }
}

@Composable
fun cardItems(
    state: CardReaderState,
    onSavedCardClicked: (cardToken: String?) -> Unit,
    onDeleteClicked: (cardToken: String?, index: Int) -> Unit,
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        userScrollEnabled = true,
    ) {
        state.savedCards?.size?.let {
            items(it) { filter ->
                Column(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .height(64.dp)
                        .background(
                            color = Color(0xFFF2F5F8),
                            shape = RoundedCornerShape(size = 5.dp),
                        )
                        .padding(start = 15.dp)
                        .clickable {
//                            Toast
//                                .makeText(
//                                    context,
//                                    "${state.savedCards?.get(index)?.external_id}",
//                                    Toast.LENGTH_SHORT,
//                                )
//                                .show()
                            onSavedCardClicked(state.savedCards?.get(filter)?.external_id)
                            AppState1.savedCardDetails = state.savedCards?.get(filter)
                        },

                    ) {

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.savedCards!!.get(filter).image)
                            .size(Size.ORIGINAL) // Set the target size to load the image at.
                            .build(),
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    onDeleteClicked(
                                        state.savedCards?.get(filter)?.external_id,
                                        filter,
                                    )
                                },
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = null,
                        )

                        Column(modifier = Modifier.fillMaxSize()) {
                            Image(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(45.dp)
                                    .padding(top = 15.dp, end = 25.dp),
                                painter = painter
                                    ?: rememberVectorPainter(Icons.Default.CreditCard),
                                //            painter = painterResource(id = R.drawable.card_visa),
                                contentDescription = "image description",
                            )

                            Text(
                                text = state.savedCards[filter].card_number,
                                //            text = "**** 1234",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF000000),
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun cardItem(
    index: Int,
    state: CardReaderState,
    onSavedCardClicked: (cardToken: String?) -> Unit,
    onDeleteClicked: (cardToken: String?, index: Int) -> Unit,
) {

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(start = 15.dp)
            .height(64.dp)
            .background(
                color = Color(0xFFF2F5F8),
                shape = RoundedCornerShape(size = 5.dp),
            )
            .padding(start = 15.dp)
            .clickable {
//                Toast
//                    .makeText(
//                        context,
//                        "${state.savedCards?.get(index)?.external_id}",
//                        Toast.LENGTH_SHORT
//                    )
//                    .show()
                onSavedCardClicked(state.savedCards?.get(index)?.external_id)
                AppState1.savedCardDetails = state.savedCards?.get(index)
            },

        ) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(state.savedCards!!.get(index).image)
                .size(Size.ORIGINAL) // Set the target size to load the image at.
                .build(),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        onDeleteClicked(state.savedCards?.get(index)?.external_id, index)
                    },
                painter = painterResource(id = R.drawable.close), contentDescription = null,
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier
                        .width(70.dp)
                        .height(45.dp)
                        .padding(top = 15.dp, end = 25.dp),
                    painter = painter ?: rememberVectorPainter(Icons.Default.CreditCard),
//            painter = painterResource(id = R.drawable.card_visa),
                    contentDescription = "image description",
                )

                Text(
                    text = state.savedCards[index].card_number,
//            text = "**** 1234",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

fun onCrossButtonClick(index: Int, state: CardReaderState) {
    // Handle item removal here
    // For example, you can remove the item from the list or update the state
    // Here, I assume `state.savedCards` is a mutable list, and you want to remove the item
    state.savedCards?.let {
        if (index in it.indices) {
            it.removeAt(index)
        }
    }
}


@NexgoN6Preview
@Composable
fun CardScannerHintOverlayPreview() {
    LedgerGreenTheme {
        CardReaderScreen(
            CardReaderState(
                cardReaderResult = null,
                onMessageShown = { },
                navigateNext = false,
                onNavigateNextConsumed = { },
                savedCards = null,
                isLoading = false,
                success = false,
            ),
            onSavedCardClicked = { },
            onDeleteClicked = { cardToken, index -> },
            dialogClose = {},
            onStart = { },
            onStop = { },
            onEnterManually = { },
            onNavigateNext = { },
        )
    }
}

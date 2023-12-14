package com.ledgergreen.terminal.ui.home.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun PaymentSuccessfulDialog(

    from: String,
    amount: String,
    onPaymentDone: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onReject,
    ) {
        Surface(modifier, shape = RoundedCornerShape(15.dp)) {

            Box {
                if (from.equals("goods&service"))
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(300.dp),
                        painter = painterResource(id = R.drawable.payment_background),
                        contentDescription = null,
                    )

                Column(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)) {

                    var xferText = ""
                    if (from.equals("goods&service")) {
                        Image(
                            painter = painterResource(id = R.drawable.xflogo),
                            contentDescription = "xflogo",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(260.dp)
                                .padding(5.dp)
                        )
                        xferText = "Xfer has been delivered"
                    } else if (from.equals("fromContactless"))
                        xferText = "SMS sent Successfully"
                    else
                        xferText = "Wallet Loaded Successfully"



                    Text(
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .fillMaxWidth(),
                        text = xferText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                        )
                    )

//                Text(
//                    modifier =Modifier.align(CenterHorizontally).fillMaxWidth().padding(top=5.dp),
//                    text = "Reference Number : $refNo",
//                    style = TextStyle(
//                        fontSize = 14.sp,
//                        lineHeight = 20.sp,
//                        fontWeight = FontWeight(400),
//                        color = Color(0xFF000000),
//                        textAlign = TextAlign.Center,
//                    )
//                )
                    var newAmt = ""

                    newAmt = if (amount.contains("$"))
                        amount.drop(1)
                    else
                        amount

                    Text(
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        text = "Amount : ${newAmt.toMoney()!!.toCurrencyString()}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                        ),
                    )

                    Column() {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                            onClick = onPaymentDone,
                            colors = ButtonDefaults.buttonColors(Color(0xFF0068FF)),
                            shape = RoundedCornerShape(5.dp),
                            content = {
                                Text(
                                    "Back to Home",
                                    color = Color.White,
                                    style = MaterialTheme.typography.h6
                                )
                            },
                        )
                    }
                }
            }

        }
    }
}

//@Composable
//fun RowScope.TableCell(
//    text: String,
//    weight: Float,
//) {
//    Text(
//        text = text,
//        Modifier
//            .border(1.dp, Color.Black)
//            .weight(weight)
//            .padding(8.dp),
//    )
//}


@NexgoN6Preview
@Composable
fun PaymentSuccessfulDialogPreview() {
    LedgerGreenTheme {
        PaymentSuccessfulDialog(
            from="goods&service",
            amount = "165",
            onPaymentDone = { },
            onReject = { },
        )
    }
}

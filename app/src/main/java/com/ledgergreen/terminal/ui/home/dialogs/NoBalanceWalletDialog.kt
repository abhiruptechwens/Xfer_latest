package com.ledgergreen.terminal.ui.home.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun NoBalanceWalletDialog(

    name: String,
    amount: String,
    onClicked: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onReject,

    ) {
        Surface(
            modifier
                .reportActivity(), shape = RoundedCornerShape(15.dp)) {

            Box {

                Icon(modifier = Modifier.align(Alignment.TopEnd).clickable { onReject() },
                    painter = painterResource(id = R.drawable.close), contentDescription = null)

                Column(modifier = Modifier.padding(16.dp)) {

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

                    Column() {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            onClick = onClicked,
                            colors = ButtonDefaults.buttonColors(Color(0xE6011C4E)),
                            shape = RoundedCornerShape(5.dp),
                            content = {
                                Text(
                                    "Load ${amount.toMoney()!!.toCurrencyString()}",
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
fun NoBalanceWalletDialogPreview() {
    LedgerGreenTheme {
        NoBalanceWalletDialog(
            name = "Cyrus",
            amount = "50",
            onClicked = { },
            onReject = { },
        )
    }
}

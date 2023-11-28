package com.ledgergreen.terminal.ui.home.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Suppress("ModifierMissing")
@Composable
fun CustomerConfirmationDialog(
    document: String,
    state: String,
    phoneNo: String,
    name: String,
    onConfirmCustomer: () -> Unit,
    onReject: () -> Unit,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onReject,
    ) {
        Surface(modifier.reportActivity(), shape = RoundedCornerShape(15.dp)) {

            Column(modifier = Modifier.padding(16.dp),) {
                Text(
                    stringResource(R.string.your_information).titleCase(),
                    style = MaterialTheme.typography.overline,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(top = 16.dp),
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp)
                        .height(1.dp) // You can adjust the height of the line
                        .background(Color.Gray),
                )

                Column() {

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = R.drawable.person_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )

                        Text(
                            stringResource(R.string.customer_name).titleCase(),
                            modifier = Modifier.padding(start = 5.dp),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                        )
//                Spacer(Modifier.height(16.dp))
                        Text(

                            name,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.End,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = R.drawable.document_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )

                        Text(
                            stringResource(R.string.document_id).titleCase(),
                            modifier = Modifier.padding(start = 5.dp),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                        )
//                Spacer(Modifier.height(16.dp))
                        Text(
                            document,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = R.drawable.state_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                        Text(
                            stringResource(R.string.state_name).titleCase(),
                            modifier = Modifier.padding(start = 5.dp),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                        )
//                Spacer(Modifier.height(16.dp))
                        Text(state,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.End)
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter = painterResource(id = R.drawable.phone_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                        Text(
                            stringResource(R.string.phone_no).titleCase(),
                            modifier = Modifier.padding(start = 5.dp),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                        )
//                Spacer(Modifier.height(16.dp))
                        Text(
                            phoneNo,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                            .height(50.dp),
                        onClick = onConfirmCustomer,
                        colors = ButtonDefaults.buttonColors(Color(0xFF003A8C)),
                        shape = RoundedCornerShape(15.dp),
                        content = { Text(stringResource(R.string.next),color = Color.White) },
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(15.dp)
                            .clickable { onScanAgain()},
                        text = stringResource(R.string.scan_again),
                        fontWeight = Bold,
                        color = Color(0xFF003A8C)
                    )
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
fun CustomerConfirmationDialogPreview() {
    LedgerGreenTheme {
        CustomerConfirmationDialog(
            document = "abcdefghi",
            state = "USA",
            phoneNo = "12345678901",
            name = "Bruce Lee",
            onConfirmCustomer = { },
            onReject = { },
            onScanAgain = { },
        )
    }
}

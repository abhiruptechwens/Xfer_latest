package com.ledgergreen.terminal.ui.home.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.supportdialog.SupportDialog
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Suppress("ModifierMissing")
@Composable
fun CustomerConfirmationDialog(
    document: String,
    state: String?,
    phoneNo: String,
    name: String,
    onConfirmCustomer: () -> Unit,
    onReject: () -> Unit,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onReject,
    ) {

        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
        )
        Surface(
            modifier
                .reportActivity()
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        style = stroke,
                        cornerRadius = CornerRadius(5.dp.toPx()),
                    )
                }, shape = RoundedCornerShape(5.dp),) {

            Box {

                Icon(
                    modifier = Modifier
//                        .background(
//                            color = Color.Red,
//                            shape = RoundedCornerShape(bottomStart = 5.dp)
//                        )
                        .padding(3.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            onClose()
                        },
                    tint = Color.Black,
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = null,
                )

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
                        .height(360.dp)
                ) {
                    Text(
                        stringResource(R.string.your_information).titleCase(),
                        fontSize = 18.sp,
                        fontWeight = Bold,
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

                        Column(modifier = Modifier) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.person_icon_light),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside
                                )

                                Text(
                                    stringResource(R.string.customer_name).titleCase(),
                                    modifier = Modifier.padding(start = 5.dp).width(110.dp),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Left,
                                )
//                Spacer(Modifier.height(16.dp))
                            Text(

                                name,
                                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Left,
                            )
                            }

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.document_icon),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside
                                )

                                Text(
                                    stringResource(R.string.document_id).titleCase(),
                                    modifier = Modifier.padding(start = 5.dp).width(110.dp),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Left,
                                )
//                Spacer(Modifier.height(16.dp))
                            Text(
                                document,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Left
                            )
                            }
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.state_icon),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside
                                )
                                Text(
                                    stringResource(R.string.state_name).titleCase(),
                                    modifier = Modifier.padding(start = 5.dp).width(110.dp),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Left,
                                )
//                Spacer(Modifier.height(16.dp))
                            if (state != null) {
                                Text(
                                    state,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Left
                                )
                            } else
                                Text(
                                    "",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Left
                                )
                            }
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.phone_icon),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside
                                )
                                Text(
                                    stringResource(R.string.phone_no).titleCase(),
                                    modifier = Modifier.padding(start = 5.dp).width(110.dp),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Left,
                                )
//                Spacer(Modifier.height(16.dp))
                            Text(
                                phoneNo,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Left
                            )
                            }

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp)
                                    .height(70.dp),
                                elevation = ButtonDefaults.elevation(10.dp),
                                onClick = onConfirmCustomer,
                                colors = ButtonDefaults.buttonColors(Color(0xFF003A8C)),
                                shape = RoundedCornerShape(5.dp),
                                content = { Text("Continue", color = Color.White, fontSize = 16.sp) },
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 15.dp)
                                    .clickable { onScanAgain() },
                                text = stringResource(R.string.scan_again),
                                fontWeight = Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF003A8C)
                            )


                            val openDialog = remember { mutableStateOf(false) }

                            if (openDialog.value) {
                                SupportDialog(
                                    onDismiss = {
                                        openDialog.value = false
                                    },
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(15.dp)
                                    .clickable { openDialog.value = true },
                                text = "Not you? Contact support.",
                                fontWeight = Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
//                        Divider(thickness = 1.dp, color = Color.Black)
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
fun CustomerConfirmationDialogPreview() {
    LedgerGreenTheme {
        CustomerConfirmationDialog(
            document = "abcdefghi",
            state = "USA",
            phoneNo = "12345678901",
            name = "MICHAEL JOSEPH LUCERO",
            onConfirmCustomer = { },
            onReject = { },
            onScanAgain = { },
            onClose = { },
        )
    }
}

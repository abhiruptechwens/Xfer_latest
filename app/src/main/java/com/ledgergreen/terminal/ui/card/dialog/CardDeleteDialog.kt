package com.ledgergreen.terminal.ui.card.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.home.dialogs.PaymentSuccessfulDialog
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun CardDeleteDialog (
onClose: () -> Unit,
modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onClose,
    ) {
        Surface(modifier, shape = RoundedCornerShape(15.dp)) {

            Column(modifier = Modifier.padding(16.dp)) {

                var xferText = ""
                    xferText = "Card Deleted Successfully"

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
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

                Column() {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(Color(0xFF0068FF)),
                        shape = RoundedCornerShape(5.dp),
                        content = { Text("Ok",color = Color.White, style = MaterialTheme.typography.h6) },
                    )
                }
            }

        }
    }
}

@NexgoN6Preview
@Composable
fun CardDeleteDialogPreview() {
    LedgerGreenTheme {
        CardDeleteDialog(
            onClose = { },
        )
    }
}

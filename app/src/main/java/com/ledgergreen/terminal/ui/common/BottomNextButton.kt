package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.R

@Preview
@Composable
fun BottomNextButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.next).uppercase(),
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    val backgroundModifier = if (enabled) {
        Modifier
            .background(Color(0xFFFF0043A5), shape = RoundedCornerShape(5.dp))
    } else {
        Modifier
            .background(Color.DarkGray, shape = RoundedCornerShape(5.dp))
    }

    Row(
        modifier = Modifier
            .then(modifier)
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp)
            .width(333.dp)
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFFF0043A5),
                shape = RoundedCornerShape(size = 5.dp),
            )
            .then(backgroundModifier)
//            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
            .clickable {
                if (enabled)
                    onClick()
            },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            text = text,
            style = TextStyle(
                fontSize = 21.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(600),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )

    }
//    Button(
//        colors = ButtonDefaults.buttonColors(Color(0x66001F43)),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(60.dp)
//            .then(modifier)
//            .border(0.5.dp,shape = RoundedCornerShape(15.dp),color = Color(0xFF6DA9FF)),
//        content = { Text(text, color = Color.White) },
//        enabled = enabled,
//        onClick = onClick,
//    )
}

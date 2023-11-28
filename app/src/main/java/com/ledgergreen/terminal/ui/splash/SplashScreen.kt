package com.ledgergreen.terminal.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun SplashScreen(
    appInfo: String,
    modifier: Modifier = Modifier,
) {

    Box {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF06478D))

                .then(modifier),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(modifier = Modifier.weight(1f))
            AppLogo(
                Modifier
                    .size(210.dp)
//                .align(Alignment.CenterHorizontally)


            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier,
                text = "Version: 2.0.000.123",
                style = MaterialTheme.typography.caption.copy(
                    color = Color(0xB2BEB9B9),
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                text = "Xfer powered by West Town Bank & Trust",
                style = MaterialTheme.typography.caption.copy(
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Image(painter = painterResource(id = R.drawable.roundradianttop),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart))
        Image(painter = painterResource(id = R.drawable.roundradiantbottom),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd)
            )
    }
}

@NexgoN6Preview
@Composable
fun SplashScreenPreview() {
    LedgerGreenTheme {
        val appInfo = "Version 1.0.0 code 25"
        SplashScreen(appInfo)
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {

    Box(
        Modifier
            .width(268.dp)
            .height(268.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 268.dp))) {
        Image(
            modifier = modifier
                .fillMaxSize()
                .then(Modifier.align(Alignment.Center)), // Center the image
            painter = painterResource(id = R.drawable.applogo),
            contentDescription = "logo",
        )
    }
}

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
import com.ledgergreen.terminal.BuildConfig
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
                .background(color = Color.White)

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
//            Text(
//                modifier = Modifier,
//                text = "Version: $BuildConfig.VERSION_NAME",
//                style = MaterialTheme.typography.caption.copy(
//                    color = Color(0xB2BEB9B9),
//                ),
//                textAlign = TextAlign.Center,
//            )
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                text = "Xfer powered by West Town Bank & Trust",
                style = MaterialTheme.typography.caption.copy(
                    color = Color.Black,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Image(painter = painterResource(id = R.drawable.top_lines),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd))
        Image(painter = painterResource(id = R.drawable.botton_lines),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomStart)
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

        Image(
            modifier = modifier
                .fillMaxSize(), // Center the image
            painter = painterResource(id = R.drawable.round_logo),
            contentDescription = "logo",
        )
}

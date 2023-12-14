package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.Signature
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView
import timber.log.Timber
@Preview
@Composable
fun SignatureView(
    modifier: Modifier = Modifier,
    onSigned: (Signature?) -> Unit,
) {
    Box(modifier = modifier) {
        var signaturePadAdapter: SignaturePadAdapter? = null

        var showHint by remember { mutableStateOf(true) }

        SignaturePadView(
            modifier = Modifier.padding(top = 8.dp),
            onReady = { signaturePadAdapter = it },
            penColor = MaterialTheme.colors.onSurface,
            onStartSigning = {
                if (showHint) {
                    showHint = false
                }
            },
            onSigning = { },
            onSigned = {
                Timber.d("onSigned")
                signaturePadAdapter?.getSignatureBitmap()?.let {
                    onSigned(Signature(it))
                }
            },
            onClear = {
                Timber.d("onClear")
            },
        )

        if (showHint) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.sign_here), style = MaterialTheme.typography.h6.copy(
                    fontSize = 32.sp,
                    color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                    fontWeight = FontWeight.Light,
                )
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 10.dp)
                .clickable {
                    signaturePadAdapter?.clear()
                    onSigned(null)
                },
            painter = painterResource(id = R.drawable.clear_red_btn),
            contentDescription = null
        )
    }
}

package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.ui.common.phonecountryselectiondialog.SelectCountryDialog


@Preview
@Composable
fun PhoneNumberTextFieldWithLeadingIcon(
    phoneNumber: PhoneNumber,
    onPhoneChanged: (String) -> Unit,
    onCountryChanged: (CountryPhoneCode) -> Unit,
    label: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {

    val openSelectCountryDialog = remember { mutableStateOf(false) }
    val inputMask = phoneNumber.getPhoneMask()

    if (openSelectCountryDialog.value) {
        SelectCountryDialog(
            onClosed = { selectedPhoneCountry ->
                if (selectedPhoneCountry != null) {
                    if (selectedPhoneCountry.isUSCode()) {
                        // ensure phone in field is not longer than US phone length
                        onPhoneChanged(phoneNumber.phone.take(PhoneNumber.USExactPhoneLength))
                    }
                    onCountryChanged(selectedPhoneCountry)
                }
                openSelectCountryDialog.value = false
            },
        )
    }

    var showPassword by remember{ mutableStateOf(false) }

    Text(
        modifier = modifier,
        text = "Enter Phone number",
        style = TextStyle(
            fontSize = 18.sp,
            color = Color.Black,
        ),
    )

    Box(modifier) {

        var isPlaceholderVisible by remember { mutableStateOf(true) }

        isPlaceholderVisible = phoneNumber.phone.isEmpty()

        if (isPlaceholderVisible) {
            Text(
                modifier = Modifier.padding(start = 65.dp, top = 8.dp),
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF9EA0A2),
                ),
            )
        }

        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {

            isPlaceholderVisible = phoneNumber.phone.isEmpty()

            Box(
                Modifier.size(60.dp,30.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable(enabled = enabled) {
                                openSelectCountryDialog.value = true
                            },
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "+${phoneNumber.phoneCode}",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                modifier = Modifier.size(16.dp),
                                contentDescription = "dropdown icon",
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            BasicTextField(
                value = phoneNumber.phone,
                enabled = enabled,
                onValueChange = { value ->
                    isPlaceholderVisible = value.isEmpty()
                    onPhoneChanged(value.take(inputMask.mask.count { it == inputMask.maskChar }))

                },
                keyboardOptions = keyboardOptions,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )

            Image(
                modifier = Modifier
                    .size(25.dp),
                painter = painterResource(id = R.drawable.phone_icon),
                contentDescription = null,

                )


        }
        Divider(
            color = if (isError) Color.Red else Color.Black, thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}


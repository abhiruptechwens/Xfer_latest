package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.data.model.phone.PhoneNumber.Companion.USExactPhoneLength
import com.ledgergreen.terminal.ui.common.phone.MaskVisualTransformation
import com.ledgergreen.terminal.ui.common.phonecountryselectiondialog.SelectCountryDialog
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun PhoneTextField(
    phoneNumber: PhoneNumber,
    onPhoneChanged: (String) -> Unit,
    onCountryChanged: (CountryPhoneCode) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Done,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val openSelectCountryDialog = remember { mutableStateOf(false) }
    val inputMask = phoneNumber.getPhoneMask()

    if (openSelectCountryDialog.value) {
        SelectCountryDialog(
            onClosed = { selectedPhoneCountry ->
                if (selectedPhoneCountry != null) {
                    if (selectedPhoneCountry.isUSCode()) {
                        // ensure phone in field is not longer than US phone length
                        onPhoneChanged(phoneNumber.phone.take(USExactPhoneLength))
                    }
                    onCountryChanged(selectedPhoneCountry)
                }
                openSelectCountryDialog.value = false
            },
        )
    }

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Black, // Set the focused outline color
        unfocusedBorderColor = Color.Black, // Set the unfocused outline color
        cursorColor = Color.Black,
        textColor = Color.Black// Set the cursor color
    )

    OutlinedTextField(
        modifier = modifier,
        value = phoneNumber.phone,
        enabled = enabled,
        onValueChange = { value ->
            onPhoneChanged(value.take(inputMask.mask.count { it == inputMask.maskChar }))
        },
        leadingIcon = {
            Box(
                Modifier.size(90.dp, 56.dp),
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
        },
        colors = colors,
        label = { Text(stringResource(R.string.enter_phone_number),color = Color.Black) },
        keyboardOptions = keyboardOptions,
        isError = isError,
        keyboardActions = keyboardActions,
        visualTransformation = if (phoneNumber.isUSPhone()) MaskVisualTransformation(
            inputMask.mask,
            inputMask.maskChar,
        ) else VisualTransformation.None,
    )
}

@Preview
@Composable
fun PhoneTextFieldDefaultPreview() {
    LedgerGreenTheme {
        PhoneTextField(
            phoneNumber = PhoneNumber.default,
            onPhoneChanged = {},
            onCountryChanged = {},
        )
    }
}

@Preview
@Composable
fun PhoneTextFieldMaxPhoneCodePreview() {
    LedgerGreenTheme {
        PhoneTextField(
            phoneNumber = PhoneNumber(
                phone = "1234567",
                countryCode = CountryPhoneCode(
                    "bb",
                    "Barbados",
                    1246,
                ),
            ),
            onPhoneChanged = {},
            onCountryChanged = {},
        )
    }
}

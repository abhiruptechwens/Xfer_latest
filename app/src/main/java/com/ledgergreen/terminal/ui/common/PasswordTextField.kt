package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordTextField(
    password: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    label: String,

    error: String?,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
) {

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFF4D4D4D), // Set the focused outline color
        unfocusedBorderColor = Color(0xFF4D4D4D), // Set the unfocused outline color
        cursorColor = Color(0xFF4D4D4D),
        textColor = Color(0xFF4D4D4D)// Set the cursor color
    )
    var showPassword by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier,
        value = password,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),

        colors = colors,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(error ?: label, color = Color(0xFF4D4D4D)) },
        trailingIcon = {
            Icon(
                Icons.Default.RemoveRedEye,
                contentDescription = null,
                tint = Color(0xFF0043A5),
                modifier = Modifier.clickable {
                    showPassword = !showPassword
                },
            )
        },
    )
}

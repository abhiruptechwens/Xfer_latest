package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.R

@Composable
fun TextFieldWithUnderlineAndTrailingIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    iconResId: Int,
    fontSize:Int = 14,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    iconTap: Boolean = false
) {

    var showPassword by remember{ mutableStateOf(false) }

    Box(modifier) {

        var isPlaceholderVisible by remember { mutableStateOf(true) }

        isPlaceholderVisible = value.isEmpty()

        if (isPlaceholderVisible) {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
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
            verticalAlignment = Alignment.CenterVertically,
        ) {

            isPlaceholderVisible = value.isEmpty()

            BasicTextField(
                value = value,
                enabled = enabled,
                onValueChange = {
                    isPlaceholderVisible = it.isEmpty()
                    onValueChange(it)

                },
                keyboardOptions = keyboardOptions,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = fontSize.sp,
                    color = Color.Black,
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                visualTransformation = if (iconTap)
                    if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation()
                else visualTransformation,
            )

            Image(
                modifier = Modifier
                    .size(20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (iconTap)
                                    showPassword = !showPassword
                            }
                        )
                    },
                painter = if (iconTap)
                    if (showPassword) painterResource(id = R.drawable.eye_open)
                    else painterResource(id = iconResId)
                else painterResource(id = iconResId),
                contentDescription = null,

                )
        }
        Divider(
            color = if (isError) Color.Red else Color.Black, thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Preview
@Composable
fun PreviewTextFieldWithUnderlineAndTrailingIcon() {
    val text = "t"
    val onValueChange: (String) -> Unit = {}
    val label = "Label"
    val iconResId = R.drawable.person_icon // Replace with your icon resource

    Surface {
        TextFieldWithUnderlineAndTrailingIcon(
            value = text,
            onValueChange = onValueChange,
            label = label,
            iconResId = iconResId,
        )
    }
}

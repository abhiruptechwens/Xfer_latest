package com.ledgergreen.terminal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun LGTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    shape: Shape = RoundedCornerShape(15),
) {
    Column(modifier) {
//        label?.let { it() }

        OutlinedTextFieldBackground(color = Color.White, isError = isError) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(textAlign = TextAlign.Left,
                    fontSize = 13.sp),
                placeholder = placeholder,
//                leadingIcon = leadingIcon,
                trailingIcon = leadingIcon,
//                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                modifier = Modifier,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    backgroundColor = Color.White
                )

            )


        }
    }
}

@Composable
fun OutlinedTextFieldBackground(
    isError: Boolean,
    color: Color,
    content: @Composable () -> Unit
) {
    // This box just wraps the background and the OutlinedTextField
    Box {
        // This box works as background
        Box(
            modifier = Modifier
                .matchParentSize() // adding some space to the label
                .background(
                    color,
                    // rounded corner to match with the OutlinedTextField
                    shape = RoundedCornerShape(5.dp)
                )
        )
        // OutlineTextField will be the content...
        content()
        Divider( color = if(isError) Color.Red else Color.Black, thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Preview(backgroundColor = 0xFF00B3FF)
@Composable
fun LGTextFieldPreview() {
    LedgerGreenTheme {
        LGTextField(
            value = "Hello",
            onValueChange = {},
            label = { Text("Label") }
        )
    }
}

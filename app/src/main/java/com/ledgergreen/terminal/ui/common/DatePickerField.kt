package com.ledgergreen.terminal.ui.common

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

fun LocalDate.dateInputString(): String =
    this.toJavaLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

@Composable
fun DatePickerField(
    value: LocalDate?,
    onChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            onChanged(
                LocalDate(
                    year = selectedYear,
                    monthNumber = selectedMonth + 1,
                    dayOfMonth = selectedDayOfMonth,
                ),
            )
        },
        value?.year ?: calendar[Calendar.YEAR],
        value?.monthNumber?.let { it - 1 } ?: calendar[Calendar.MONTH],
        value?.dayOfMonth ?: calendar[Calendar.DAY_OF_MONTH],
    )

    Box(modifier = modifier) {

        val colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black, // Set the focused outline color
            unfocusedBorderColor = Color.Black, // Set the unfocused outline color
            cursorColor = Color.Black,
            textColor = Color.Black// Set the cursor color
        )
        OutlinedTextField(
            modifier = Modifier.focusable(false),
            value = value?.dateInputString() ?: "",
            onValueChange = {},
            enabled = false,
            readOnly = readOnly,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next,
            ),
            label = label,
            placeholder = placeholder,
            colors = colors,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(
                    enabled = enabled,
                    onClick = { datePickerDialog.show() },
                ),
        )
    }
}

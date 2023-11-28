package com.ledgergreen.terminal.ui.common.phone

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(
    private val mask: String,
    private val maskChar: Char,
) : VisualTransformation {

    private val maxLength = mask.count { it == maskChar }

    override fun filter(text: AnnotatedString): TransformedText {
        val annotatedString = buildAnnotatedString {
            val trimmed = if (text.length > maxLength) text.take(maxLength) else text

            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskChar) {
                    val nextDigitIndex = mask.indexOf(maskChar, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, MaskOffsetMapper(mask, maskChar))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaskVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskChar != other.maskChar) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class MaskOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}

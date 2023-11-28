package com.ledgergreen.terminal.ui.card.details

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.regex.Pattern

fun cardNumberMaskFromType(cardType: CardType, separator: String = "-"): VisualTransformation {
    return when (cardType) {
        CardType.AMERICAN_EXPRESS -> AmexCardNumberMask(separator)
        else -> DefaultCardNumberMask(separator)
    }
}

class DefaultCardNumberMask(private val separator: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return makeCardNumberFilter(text, separator)
    }

    private fun makeCardNumberFilter(text: AnnotatedString, separator: String): TransformedText {
        // format: XXXX XXXX XXXX XXXX by default
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 3 || i == 7 || i == 11) out += separator
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 3) offset
                else if (offset <= 7) offset + 1
                else if (offset <= 11) offset + 2
                else if (offset <= 16) offset + 3
                else 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 4) offset
                else if (offset <= 9) offset - 1
                else if (offset <= 14) offset - 2
                else if (offset <= 19) offset - 3
                else 16
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class AmexCardNumberMask(private val separator: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return makeCardNumberFilter(text, separator)
    }

    private fun makeCardNumberFilter(text: AnnotatedString, separator: String): TransformedText {
        // format: XXXX XXXXXX XXXXX by default for amex
        val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 3 || i == 9) out += separator
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 3) offset
                else if (offset <= 9) offset + 1
                else if (offset <= 15) offset + 2
                else 17
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 4) offset
                else if (offset <= 11) offset - 1
                else if (offset <= 17) offset - 2
                else 15
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class ExpirationDateMask : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return makeExpirationFilter(text)
    }

    private fun makeExpirationFilter(text: AnnotatedString): TransformedText {
        // format: XX/XX
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 1 -> offset
                offset <= 4 -> offset + 1
                else -> 5
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 5 -> offset - 1
                else -> 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

enum class CardType {
    VISA, MASTERCARD, AMERICAN_EXPRESS, JCB, DINNERS_CLUB, MAESTRO, DISCOVER, UNKNOWN
}

fun getCardTypeFromNumber(number: String): CardType {
    val visaRegex = Pattern.compile("^4[0-9]*\$")
    val mastercardRegex =
        Pattern.compile("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]*\$")
    val americanExpressRegex = Pattern.compile("^3[47][0-9]*\$")
    val jcbRegex = Pattern.compile("^(?:2131|1800|35)[0-9]*\$")
    val maestroRegex = Pattern.compile("^(5[06789]|6)[0-9]*\$")
    val dinnersRegex = Pattern.compile("^3(?:0[0-59]|[689])[0-9]*\$")
    val discoverRegex =
        Pattern.compile("^(6011|65|64[4-9]|62212[6-9]|6221[3-9]|622[2-8]|6229[01]|62292[0-5])[0-9]*\$")

    return when {
        visaRegex.matcher(number).matches() -> CardType.VISA
        mastercardRegex.matcher(number).matches() -> CardType.MASTERCARD
        americanExpressRegex.matcher(number).matches() -> CardType.AMERICAN_EXPRESS
        jcbRegex.matcher(number).matches() -> CardType.JCB
        maestroRegex.matcher(number).matches() -> CardType.MAESTRO
        dinnersRegex.matcher(number).matches() -> CardType.DINNERS_CLUB
        discoverRegex.matcher(number).matches() -> CardType.DISCOVER
        else -> CardType.UNKNOWN
    }
}

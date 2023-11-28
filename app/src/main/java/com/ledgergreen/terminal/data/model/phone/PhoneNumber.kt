package com.ledgergreen.terminal.data.model.phone

data class PhoneNumber(
    val phone: String,
    val countryCode: CountryPhoneCode,
) {
    companion object {
        const val minPhoneLength = 6
        const val USExactPhoneLength = 10
        const val maskChar: Char = '0'
        const val USPhoneMask = "000 000 0000"

        // max 17 digits for non US number, excluding country code
        // https://github.com/google/libphonenumber/blob/master/FAQ.md#what-is-the-maximum-and-minimum-length-of-a-phone-number
        const val nonUSPhoneMask = "00000000000000000"

        val default = PhoneNumber(
            "",
            CountryCodes.defaultUSPhoneCode,
        )
    }

    val phoneCode: Int
        get() = countryCode.phoneCode

    fun isValidPhoneLength(): Boolean =
        when {
            isUSPhone() -> phone.length == USExactPhoneLength
            else -> phone.length >= minPhoneLength
        }

    fun isUSPhone(): Boolean = countryCode.isUSCode()

    fun getPhoneMask(): PhoneInputMask =
        PhoneInputMask(
            mask = if (isUSPhone()) USPhoneMask else nonUSPhoneMask,
            maskChar = maskChar,
        )
}

data class PhoneInputMask(val mask: String, val maskChar: Char) {
    val totalLength: Int = mask.length
    val charsLength: Int = mask.count { it == maskChar }
}

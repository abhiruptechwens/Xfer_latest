package com.ledgergreen.terminal.scanner.parser.driverlicense.models

import com.ledgergreen.terminal.scanner.parser.driverlicense.categories.Gender
import com.ledgergreen.terminal.scanner.parser.driverlicense.categories.IssuingCountry
import java.util.Date

/**
 * A model for storing the parsed license data.
 * */
data class License(
    var firstName: String? = null,
    var middleNames: List<String> = listOf(),
    var lastName: String? = null,

    var expirationDate: Date? = null,
    var issuedDate: Date? = null,
    var birthdate: Date? = null,

    var gender: Gender? = null,

    var streetAddress: String? = null,
    var city: String? = null,
    var state: String? = null,
    var postalCode: String? = null,
    var country: IssuingCountry?,

    var licenseNumber: String? = null,

    var version: Int? = null,
    var pdf417Data: String? = null,
) {
    /**
     * Determines if enough of the license data is present to be useful for most things.
     */
    val isAcceptable get() = licenseNumber != null
}

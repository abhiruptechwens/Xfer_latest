package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.categories.Gender
import com.ledgergreen.terminal.scanner.parser.driverlicense.categories.IssuingCountry
import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey
import com.ledgergreen.terminal.scanner.parser.driverlicense.models.License
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
* this is a modified copy of this repository
* https://github.com/ajohnson388/DLParser-Kotlin
*/

/**
 * The primary class for parsing driver license data. This class automatically parses based on the
 * version number
 */
open class DLParser(val data: String) {
    /**
     * The base fields common to all or most version standards. This
     * field should be modified in subclasses for version-specific
     * field changes.
     * */
    internal val fields: MutableMap<FieldKey, String> = mutableMapOf(
        FieldKey.EXPIRATION_DATE to "DBA",
        FieldKey.ISSUE_DATE to "DBD",
        FieldKey.FIRST_NAME to "DAC",
        FieldKey.MIDDLE_NAME to "DAD",
        FieldKey.LAST_NAME to "DCS",
        FieldKey.BIRTH_DATE to "DBB",
        FieldKey.GENDER to "DBC",
        FieldKey.STREET_ADDRESS to "DAG",
        FieldKey.CITY to "DAI",
        FieldKey.STATE to "DAJ",
        FieldKey.POSTAL_CODE to "DAK",
        FieldKey.DRIVER_LICENSE_NUMBER to "DAQ",
        FieldKey.COUNTRY to "DCG",
        FieldKey.DRIVER_LICENSE_NAME to "DAA",
        FieldKey.GIVEN_NAME to "DCT",
    )

    /**
     * The version number detected in the driver license data or nil
     * if the data is not AAMVA compliant.
     */
//    val versionNumber get() = Utils.firstRegexMatch("\\d{6}(\\d{2})\\w+", data)?.toInt()
    val versionNumber: Int?
        get() {
            val matches = Regex("\\D(\\d{6}(\\d{2}))").find(data)
            val value = matches?.value
            return value?.substring(7)?.toIntOrNull()
        }

    protected open val unitedStatesDateFormat get() = "MMddyyyy"
    protected open val canadaDateFormat get() = "yyyyMMdd"

    private val dateFormat
        get() = when (parsedCountry) {
            IssuingCountry.UNITED_STATES -> unitedStatesDateFormat
            IssuingCountry.CANADA -> canadaDateFormat
            else -> unitedStatesDateFormat
        }

    private val versionParser
        get() = when (versionNumber) {
            1 -> VersionOneParser(data)
            2 -> VersionTwoParser(data)
            3 -> VersionThreeParser(data)
            4 -> VersionFourParser(data)
            5 -> VersionFiveParser(data)
            6 -> VersionSixParser(data)
            7 -> VersionSevenParser(data)
            8 -> VersionEightParser(data)
            9 -> VersionNineParser(data)
            else -> DLParser(data)
        }

    fun parse(): License {
        val version = versionNumber
        val parser = versionParser
        return License(
            firstName = parser.parsedFirstName,
            middleNames = parser.parsedMiddleNames,
            lastName = parser.parsedLastName,
            issuedDate = parseDate(FieldKey.ISSUE_DATE),
            birthdate = parser.parseDate(FieldKey.BIRTH_DATE),
            gender = parser.parsedGender,
            streetAddress = parser.parseString(FieldKey.STREET_ADDRESS),
            city = parser.parseString(FieldKey.CITY),
            state = parser.parseString(FieldKey.STATE),
            postalCode = parser.parsedPostalCode,
            country = parser.parsedCountry,
            licenseNumber = parser.parseString(FieldKey.DRIVER_LICENSE_NUMBER),
            version = version,
            pdf417Data = data,
            expirationDate = parseDate(FieldKey.EXPIRATION_DATE),
        )
    }

    internal open fun parseString(key: FieldKey): String? {
        fields[key]?.let {
            return Regex("$it(.+)\\b").find(data)?.value?.removePrefix(it)
        } ?: return null
    }

    internal open fun parseDate(key: FieldKey): Date? {
        val dateString = parseString(key)
        if (dateString.isNullOrEmpty()) return null else if (dateString == "SIM") return SimpleDateFormat(dateFormat, Locale.US).parse("02052029")
        return SimpleDateFormat(dateFormat, Locale.US).parse(dateString)
    }

    protected open val parsedFirstName
        get() =
            parseString(FieldKey.FIRST_NAME)
                ?: parseString(FieldKey.GIVEN_NAME)?.split(",")?.firstOrNull()?.trim()
                ?: parseString(FieldKey.DRIVER_LICENSE_NAME)?.split(",")?.firstOrNull()?.trim()

    protected open val parsedMiddleNames: List<String>
        get() {
            parseString(FieldKey.MIDDLE_NAME)?.let {
                return listOf(it)
            }

            parseString(FieldKey.GIVEN_NAME)?.let {
                val parts = it.split(",")
                return parts.drop(1).map { it.trim() }
            }

            parseString(FieldKey.DRIVER_LICENSE_NAME)?.let {
                val parts = it.split(",")
                return if (parts.size <= 2) {
                    emptyList()
                } else {
                    parts.drop(1).dropLast(1).map { it.trim() }
                }
            }
            return listOf()
        }

    protected open val parsedLastName
        get() = parseString(FieldKey.LAST_NAME)
            ?: parseString(FieldKey.DRIVER_LICENSE_NAME)?.split(",")?.lastOrNull()?.trim()

    protected open val parsedCountry: IssuingCountry?
        get() {
            return IssuingCountry.of(parseString(FieldKey.COUNTRY) ?: return null)
        }

    protected open val parsedGender: Gender?
        get() {
            return Gender.of(parseString(FieldKey.GENDER) ?: return null)
        }

    /**
     * Returns the postal code in 12345-6789 format or 12345 if the last 4 digits are 0.
     */
    protected open val parsedPostalCode: String?
        get() {
            val rawCode = parseString(FieldKey.POSTAL_CODE)
            val firstPart = rawCode?.substring(0, 5) ?: return null
            val secondPart = rawCode.substring(5)

            secondPart.takeIf { it != "0000" }?.let { return firstPart.plus("-").plus(it) }
                ?: return firstPart
        }
}

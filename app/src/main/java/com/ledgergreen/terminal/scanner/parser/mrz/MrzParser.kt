package com.ledgergreen.terminal.scanner.parser.mrz

import com.ledgergreen.terminal.scanner.Document
import com.ledgergreen.terminal.scanner.DocumentParser
import com.neovisionaries.i18n.CountryCode
import fr.coppernic.lib.mrz.MrzParser
import fr.coppernic.lib.mrz.parser.MrzParserOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern
import javax.inject.Inject
import kotlinx.datetime.LocalDate
import timber.log.Timber

class MrzParser @Inject constructor() : DocumentParser {

    private val parser = MrzParser()
    private val defaultOptions = MrzParserOptions(lenient = true)

    override fun isApplicable(raw: String): Boolean {
        return runCatching {
            val result = parser.parse(
                s = raw.heal(),
                opt = defaultOptions,
            )
            result.finalHashValid
        }.fold(
            onSuccess = { it },
            onFailure = { false },
        )
    }

    override fun parse(raw: String): Result<Document> = runCatching {
        val mrzRecord = parser.parse(raw.heal(), defaultOptions)
        Document.MrzDocument(
            documentNumber = mrzRecord.documentNumber,
            firstName = mrzRecord.givenNames,
            lastName = mrzRecord.surnames,
            expiryDate = mrzRecord.expiryDate!!.toLocalDate(),
            birthDate = mrzRecord.birthdate!!.toLocalDate(),
            country = mrzRecord.countryCode.iso3166CodeToCountryName(),
            sex = mrzRecord.sex.name,
        )
    }

    private val validCharacters = Pattern.compile("[^A-Z0-9<\n]")

    /** MF returns '?(?)' at the end of the string */
    private fun String.heal() = validCharacters.matcher(this).replaceAll("")

    private fun Date.toLocalDate(): LocalDate {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateString = sdf.format(this)
        return LocalDate.parse(dateString)
    }

    private fun String.iso3166CodeToCountryName(): String = runCatching {
        CountryCode.getByCodeIgnoreCase(this).getName()
    }.fold(
        onSuccess = { it },
        onFailure = {
            Timber.w(it, "Unable to find country by code $this")
            // return country as is
            this
        },
    )
}

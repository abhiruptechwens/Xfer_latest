package com.ledgergreen.terminal.scanner

import kotlinx.datetime.LocalDate

interface DocumentParser {
    fun isApplicable(raw: String): Boolean
    fun parse(raw: String): Result<Document>
}

sealed class Document {
    abstract val documentNumber: String
    abstract val firstName: String
    abstract val lastName: String
    abstract val expiryDate: LocalDate
    abstract val birthDate: LocalDate?
    abstract val country: String
    abstract val sex: String?

    data class DriverLicense(
        override val documentNumber: String,
        override val firstName: String,
        val middleName: String,
        override val lastName: String,
        override val expiryDate: LocalDate,
        override val birthDate: LocalDate?,
        override val sex: String?,
        override val country: String,
        val state: String,
        val city: String,
        val issueDate: LocalDate,
        val address1: String,
        val postalCode: String,
    ) : Document()

    data class MrzDocument(
        override val documentNumber: String,
        override val firstName: String,
        override val lastName: String,
        override val expiryDate: LocalDate,
        override val birthDate: LocalDate,
        override val country: String,
        override val sex: String,
    ) : Document()
}

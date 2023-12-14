package com.ledgergreen.terminal.domain.scan

import androidx.compose.runtime.Immutable
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.data.network.model.CustomerIdType
import kotlinx.datetime.LocalDate

@Immutable
data class CustomerRegistrationForm(
    val idNumber: String,
    val idType: CustomerIdType,
    val firstName: String?,
    val lastName: String?,
    val birthdate: LocalDate?,
    val expirationDate: LocalDate,
    val gender: String?,
    val country: String?,
    val address1: String?,
    val city: String?,
    val state: String?,
    val issueDate: LocalDate?,
    val postalCode: String?,
    val phoneNumber: PhoneNumber?,
    val middleName: String?,
)

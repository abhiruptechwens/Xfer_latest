package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComplianceFeeRequest(
    /** Int (Only, first 6 digits required) */
    @SerialName("card_number")
    val cardNumber: String,
    val amount: Double,
)

@Serializable
data class ComplianceFee(
    val status: Boolean,
    val response: String, // success ??
    @SerialName("card_fee")
    val cardFee: Double,
    @SerialName("card_type")
    val cardType: String, // DEBIT / CREDIT
    val amount: Double,
    @SerialName("amount_plus_fee")
    val amountPlusFee: Double,
)

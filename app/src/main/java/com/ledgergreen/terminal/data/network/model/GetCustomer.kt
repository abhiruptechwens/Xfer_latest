package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCustomer(
    @SerialName("response")
    val response: CustResponse,
    @SerialName("status")
    val status: Boolean,
    @SerialName("statusCode")
    val statusCode: Int
)
@Serializable
data class CustResponse(
    val amount: String,
    val card: MutableList<Card>,
//    val lastActivityId: Int,
    val name: String,
    @SerialName("account_number")
    val accountNumber: String,
    @SerialName("customer_external_id")
    val customerExtId: String
)
@Serializable
data class Card(
    val cardNumber: String,
    val card_number: String,
    val card_type: String,
    val external_id: String,
    val image: String,
    val is_primary: Boolean
)

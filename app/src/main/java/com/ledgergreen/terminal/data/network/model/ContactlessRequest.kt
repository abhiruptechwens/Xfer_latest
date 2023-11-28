package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactlessRequest(
    @SerialName("user_id")
    val userId: Long,
    @SerialName("store_id")
    val storeId: String,
    @SerialName("customer_name")
    val customerName: String,
    @SerialName("country_code")
    val phoneCode: String,
    @SerialName("phone")
    val phoneNumber: String,
    @SerialName("send_sms")
    val sendSms: Boolean,
    val amount: Double,
)

@Serializable
data class ContactlessResponse(
    val url: String,
    @SerialName("sms_sent")
    val smsSent: Boolean,
)

package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignatureRequest(
    @SerialName("customer_id")
    val customerId: Long,
    @SerialName("user_id")
    val userId: Long,
    val signature: String,
)

@Serializable
data class SignatureResponse(
    val url: String,
    val name: String,
    val status: Boolean,
    @SerialName("activity_id")
    val activityId: Long,
)

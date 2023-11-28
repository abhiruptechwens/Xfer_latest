package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPinRequest(
    val pin: String,
    @SerialName("user_id")
    val userId: String,
)

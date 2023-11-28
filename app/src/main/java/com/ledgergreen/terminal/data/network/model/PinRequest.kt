package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PinRequest(
    val pin: String
)

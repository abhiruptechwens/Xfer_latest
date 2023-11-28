package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class StringToJsonData(
    val message: String,
    val response: ResponseX?,
    val status: Boolean?,
//    val statusCode: Int?
)

@Serializable
data class ResponseX(
    val message: String?
)

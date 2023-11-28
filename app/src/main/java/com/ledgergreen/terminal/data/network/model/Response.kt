package com.ledgergreen.terminal.data.network.model

data class Response(
    val amount: Int,
    val balance: Int,
    val created_at: String,
    val customer_account_number: String,
    val name: String,
    val order_id: String,
    val phone: String,
    val url: String
)

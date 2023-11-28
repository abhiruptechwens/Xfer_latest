package com.ledgergreen.terminal.data.model

data class CardDetails(
    val number: String,
    val year: String,
    val month: String,
    val cvv: String,
    val holderName: String,
    val billingAddressZip: String,
)

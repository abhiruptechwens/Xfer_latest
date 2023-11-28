package com.ledgergreen.terminal.data.model

data class AmountDetails(
    val order: Money,
    val tips: Money,
    val tipsType: String? = null,
)

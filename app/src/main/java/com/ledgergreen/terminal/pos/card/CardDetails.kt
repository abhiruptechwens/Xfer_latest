package com.ledgergreen.terminal.pos.card

enum class CardReaderMethod {
    RF, SWIPE, ICC,
}

data class CardDetails(
    val number: String,
    val expiry: String,
    val method: CardReaderMethod,
)

package com.ledgergreen.terminal.data.model

import androidx.core.text.isDigitsOnly

@JvmInline
value class Pin(val code: String) {
    init {
        require(code.length == 4) { "Pin code cannot be more than 4 digits" }
        require(code.isDigitsOnly()) { "Pin code can consist only from digits" }
    }
}

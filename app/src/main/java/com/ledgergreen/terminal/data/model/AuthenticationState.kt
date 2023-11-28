package com.ledgergreen.terminal.data.model

enum class AuthenticationState {
    INITIAL,
    LOGIN_REQUIRED,
    PASSWORD_RESET_REQUIRED,
    PIN_REQUIRED,
    PIN_RESET_REQUIRED,
    AUTHENTICATED,
}

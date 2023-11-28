package com.ledgergreen.terminal.ui.password

sealed class ChangePasswordEvent {
    data class Password1Change(val password: String) : ChangePasswordEvent()
    data class Password2Change(val password: String) : ChangePasswordEvent()
    object ChangePassword : ChangePasswordEvent()
    object OnErrorShown : ChangePasswordEvent()
}

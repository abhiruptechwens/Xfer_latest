package com.ledgergreen.terminal.ui.login

import androidx.compose.runtime.Immutable

@Immutable
data class LoginState(
    val login: String,
    val password: String,
    val isLoading: Boolean,
    val error: String?,
    val success: Boolean,
    val onErrorShown: () -> Unit,
)

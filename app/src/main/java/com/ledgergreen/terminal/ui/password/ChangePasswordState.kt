package com.ledgergreen.terminal.ui.password

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
@Immutable
data class ChangePasswordState(
    val password1: String,
    val password2: String,
    val fieldValidationError: String?,
    val errorMessage: String?,
    val loading: Boolean,
    val eventSink: (ChangePasswordEvent) -> Unit,
)

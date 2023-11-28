package com.ledgergreen.terminal.ui.pin

import androidx.compose.runtime.Immutable

@Immutable
data class PinState(
    val vendorName: String,
    val pinCode: String,
    val isLoading: Boolean,
    val appInfo: String,
    val error: String?,
    val success: Boolean,
    val onErrorShown: () -> Unit,
)

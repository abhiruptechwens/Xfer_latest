package com.ledgergreen.terminal.ui.amount

import androidx.compose.runtime.Immutable

@Immutable
data class AmountState(
    val amount: String,
    val proceedAvailable: Boolean,
    val navigateNext: Boolean,
    val accNo: String?,
)

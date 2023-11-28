package com.ledgergreen.terminal.ui.agreement

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.data.model.Signature

@Stable
@Immutable
data class AgreementState(
    val agreementText: String,
    val agreementAccepted: Boolean,
    val signature: Signature?,
    val navigateNext: Boolean = false,
) {
    val proceedAvailable = signature != null && agreementAccepted
}

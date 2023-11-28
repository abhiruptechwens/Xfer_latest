package com.ledgergreen.terminal.ui.common.supportdialog

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.domain.support.SupportInfo

@Stable
@Immutable
data class SupportDialogState(
    val supportInfo: SupportInfo,
) {
    companion object {
        val initial = SupportDialogState(
            SupportInfo(
                versionInfo = "",
                storeId = 0L,
                storeName = "",
                userId = 0L,
                username = "",
            ),
        )
    }
}

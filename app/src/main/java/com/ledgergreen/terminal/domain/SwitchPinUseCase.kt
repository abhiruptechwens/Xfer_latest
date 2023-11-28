package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import javax.inject.Inject

class SwitchPinUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
    private val analytics: Analytics,
) {
    operator fun invoke(isIdleLock: Boolean) {
        analytics.trackCustomAction(
            name = Actions.clearPin,
            attributes = mapOf(
                "cause" to if (isIdleLock) "Idle lock" else "User switch pin",
            ),
        )

        transactionCache.clear()
    }
}

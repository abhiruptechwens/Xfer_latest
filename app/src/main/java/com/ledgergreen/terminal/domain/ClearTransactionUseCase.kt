package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.data.TransactionCache
import javax.inject.Inject

/** Clear transaction cache and prepare a new transaction */
class ClearTransactionUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke() {
        transactionCache.clearTransaction()
    }
}

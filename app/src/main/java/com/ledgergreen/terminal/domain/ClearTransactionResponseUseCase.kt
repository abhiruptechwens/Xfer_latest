package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.data.TransactionCache
import javax.inject.Inject

class ClearTransactionResponseUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke() {
        transactionCache.clearTransactionResponse()
    }
}

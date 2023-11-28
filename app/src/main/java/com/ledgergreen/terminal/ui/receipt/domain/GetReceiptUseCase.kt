package com.ledgergreen.terminal.ui.receipt.domain

import com.ledgergreen.terminal.data.TransactionCache
import javax.inject.Inject
import kotlinx.coroutines.flow.filterNotNull

class GetReceiptUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke() = transactionCache.transactionResponse.filterNotNull()
}

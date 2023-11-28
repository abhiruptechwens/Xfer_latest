package com.ledgergreen.terminal.ui.tips.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money
import javax.inject.Inject

class GetAmountUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): Money {
        val amountDetails = transactionCache.amountDetails.value
            ?: error("AmountDetails is not set")
        return amountDetails.order
    }
}

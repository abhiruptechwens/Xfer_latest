package com.ledgergreen.terminal.ui.amount.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.AmountDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import javax.inject.Inject

class SaveAmountUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(amount: Money) {
        val amountDetails = AmountDetails(
            order = amount,
            tips = 0.0.toMoney(),
        )
        transactionCache.setAmountDetails(amountDetails)
    }
}

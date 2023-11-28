package com.ledgergreen.terminal.ui.tips.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.network.model.CustResponse
import javax.inject.Inject

class GetCustomerWalletDetailsUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
    ) {
        operator fun invoke(): CustResponse {
            val custWalletResponse = transactionCache.custWalletResponse.value
                ?: error("AmountDetails is not set")
            return custWalletResponse
        }
}

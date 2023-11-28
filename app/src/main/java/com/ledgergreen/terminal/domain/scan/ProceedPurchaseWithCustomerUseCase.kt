package com.ledgergreen.terminal.domain.scan

import com.ledgergreen.terminal.data.CustomerTransactionData
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import javax.inject.Inject

class ProceedPurchaseWithCustomerUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(customerResponse: CustomerResponse) {
        transactionCache.setCustomer(
            CustomerTransactionData(
                customerResponse.id,
                customerResponse.fullName,
            ),
        )
    }
}

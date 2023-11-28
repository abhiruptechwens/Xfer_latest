package com.ledgergreen.terminal.ui.card.details.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.AmountDetails
import javax.inject.Inject

data class TransactionInput(
    val cardNumber: String?,
    val expiryDate: String?,
    val cardHolderName: String,
    val amountDetails: AmountDetails,
)

class GetTransactionInputUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): TransactionInput {
        val cardReaderResult = transactionCache.cardReaderResult.value
        val customerFullName = transactionCache.customer.value?.fullName
            ?: error("CustomerTransactionData is not set")
        val amountDetails = transactionCache.amountDetails.value ?: error("Amount is not set")

        return TransactionInput(
            cardNumber = cardReaderResult?.number,
            expiryDate = cardReaderResult?.expiry,
            cardHolderName = customerFullName,
            amountDetails = amountDetails,
        )
    }
}

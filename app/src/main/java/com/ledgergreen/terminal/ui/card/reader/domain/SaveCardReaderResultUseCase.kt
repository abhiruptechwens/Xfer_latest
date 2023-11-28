package com.ledgergreen.terminal.ui.card.reader.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.pos.card.CardDetails
import javax.inject.Inject

class SaveCardReaderResultUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(cardReaderResult: CardDetails?) {
        transactionCache.setCardData(cardReaderResult)
    }
}

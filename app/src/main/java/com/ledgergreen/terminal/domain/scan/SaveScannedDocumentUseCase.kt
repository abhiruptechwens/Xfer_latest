package com.ledgergreen.terminal.domain.scan

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.scanner.Document
import javax.inject.Inject

class SaveScannedDocumentUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(document: Document) {
        transactionCache.setDocument(document)
    }
}

class GetScannedDocumentUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): Document? = transactionCache.document.value
}

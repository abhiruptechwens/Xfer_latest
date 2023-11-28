package com.ledgergreen.terminal.ui.agreement.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Signature
import javax.inject.Inject

class SaveSignatureUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(signature: Signature) {
        transactionCache.setSignature(signature)
    }
}

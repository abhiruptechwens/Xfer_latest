package com.ledgergreen.terminal.ui.tips.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import javax.inject.Inject

class SetTipOptionUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(tipOption: TipOption) {
        val orderAmount = transactionCache.amountDetails.value!!.order.toDouble()
        val tips = when (tipOption) {
            is TipOption.FlatTipOption -> tipOption.amount.toDouble()
            is TipOption.PercentageTipOption -> {
                orderAmount * (tipOption.percentage / 100.0)
            }
        }.toMoney()


        val amountDetails = transactionCache.amountDetails.value!!.copy(
            tips = tips,
            tipsType = tipOption.getType(),
        )

        transactionCache.setAmountDetails(amountDetails)
    }
}

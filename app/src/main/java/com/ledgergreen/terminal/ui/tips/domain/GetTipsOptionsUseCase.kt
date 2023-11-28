package com.ledgergreen.terminal.ui.tips.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.network.model.AmountType
import com.ledgergreen.terminal.data.network.model.TipsType
import javax.inject.Inject
import timber.log.Timber

sealed class TipOption {
    data class PercentageTipOption(
        val percentage: Int,
    ) : TipOption()

    data class FlatTipOption(
        val amount: Money,
        val enteredManually: Boolean = false,
    ) : TipOption()
}

fun TipOption.getType(): String =
    when {
        this is TipOption.FlatTipOption && this.enteredManually -> "Custom"
        else -> "Preset"
    }


class GetTipsOptionsUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): List<TipOption> {
        val rawOptions = (transactionCache.pinResponse.value!!.tips ?: emptyList())
        if (rawOptions.isEmpty()) {
            Timber.d("Tips options are not present")
            return emptyList()
        }

        val tipOptions = rawOptions
            .filter { it.tipsType == TipsType.Radio }
            .map { option ->
                when (option.amountType) {
                    AmountType.Percentage -> TipOption.PercentageTipOption(option.amount.toInt())
                    AmountType.Flat -> TipOption.FlatTipOption(option.amount.toMoney()!!)
                }
            }
        return tipOptions
    }
}

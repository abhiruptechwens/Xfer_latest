package com.ledgergreen.terminal.ui.card.details.domain

import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import javax.inject.Inject
import timber.log.Timber

class GetComplianceFeeUseCase @Inject constructor(
    private val apiService: ApiService,
) {
    suspend operator fun invoke(cardNumber: String, amount: Money): Money {
        Timber.i("Get convenience fee")
        // only first 6 digits of card required
        val response = apiService.complianceFee(cardNumber.take(first6Digits), amount.toDouble())
        if (response.status) {
            return response.cardFee.toMoney()
        } else {
            throw RequestException(response.response)
        }
    }

    companion object {
        private const val first6Digits = 6
    }
}

package com.ledgergreen.terminal.ui.amount

import androidx.lifecycle.ViewModel
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.ui.amount.domain.SaveAmountUseCase
import com.ledgergreen.terminal.ui.tips.domain.GetCustomerWalletDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class AmountViewModel @Inject constructor(
    @Deprecated("DO NOT USE IT HERE")
    private val transactionCache: TransactionCache,
    private val setAmount: SaveAmountUseCase,
    private val getCustDetails: GetCustomerWalletDetailsUseCase,

) : ViewModel() {

    val state = MutableStateFlow(
        try {
            AmountState(
                amount = "",
                proceedAvailable = false,
                navigateNext = false,
                accNo = getCustDetails().accountNumber
            )
        } catch (e: IllegalStateException) {
            // Handle the exception, log it, or provide a default value
            AmountState(
                amount = "",
                proceedAvailable = false,
                navigateNext = false,
                accNo = ""
            )
        },
    )

    fun onAmountChange(amountInput: String) {
        val currentAmount: Double = (state.value.amount.toDoubleOrNull() ?: 0.0) / 100
        val newAmount: Double = (amountInput.toDoubleOrNull() ?: 0.0) / 100

        // prevent user from entering amount with only zeros
        if (currentAmount == 0.0 && newAmount == 0.0) {
            return
        }

        state.value = state.value.copy(
            amount = amountInput,
            proceedAvailable = newAmount >= 1.0,
        )
    }

    fun onProceed() {
//        AppState1.inputAmount = (state.value.amount.toDouble() / 100).toMoney()
        val amountMoney = (state.value.amount.toDouble() / 100).toMoney()
        setAmount(amountMoney)
        state.value = state.value.copy(
            navigateNext = true,
        )
    }

    fun onNavigationHandled() {
        state.value = state.value.copy(
            navigateNext = false,
        )
    }

    // TODO: workaround
    fun hasTipsOptions() = transactionCache.pinResponse.value?.tips?.isNotEmpty() == true
}

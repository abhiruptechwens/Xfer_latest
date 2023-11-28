package com.ledgergreen.terminal.ui.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.domain.ClearTransactionResponseUseCase
import com.ledgergreen.terminal.domain.ClearTransactionUseCase
import com.ledgergreen.terminal.ui.card.details.domain.PerformGoodsAndServiceTransactionUseCase
import com.ledgergreen.terminal.ui.receipt.domain.GetReceiptUseCase
import com.ledgergreen.terminal.ui.tips.TipsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    getReceipt: GetReceiptUseCase,
    private val clearTransactionResponseUseCase: ClearTransactionResponseUseCase,
    private val clearTransactionUseCase: ClearTransactionUseCase,
    private val performGoodsAndServiceTransaction: PerformGoodsAndServiceTransactionUseCase,
) : ViewModel() {

    val state: StateFlow<ReceiptState> = getReceipt()
        .map {
            ReceiptState(
                orderId = it.orderId,
                totalAmount = "$" + it.orderAmount.toString(),
                amount = "$" + it.amount.toString(),
//                tipAmount = "$" + it.tips.toString(),
//                complianceFee = "$" + it.fees.toString(),
                orderDate = it.date,
//                cardNumber = it.cardNumber,
//                cardType = it.cardType,
                customerName = it.customerName,
                accountNumber = it.customerAccountNumber,
                location = it.location,
//                terminal = it.terminalId,
                customerPhoneNumber = it.customerPhone ?: "",
                signatureUrl = it.signature,
                receiptId = it.receiptId,
                customerExtId = it.customerExtId,
                onNext = false,
                orderAmount = it.orderAmount.toString(),
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ReceiptState.EMPTY)

//    private val _state = MutableStateFlow(
//        ReceiptState(
//            customerExtId = AppState1.customerExtId,
//            onNextAvailable = false
//        ),
//    )

//    val state: StateFlow<ReceiptState> get() = _state

    fun clearTransactionCache() {
        Timber.i("Transaction finished. Clear cache")
        clearTransactionResponseUseCase()
    }

    fun clearCache() {
        Timber.i("Transaction finished. Clear cache")
        clearTransactionUseCase()
    }

    fun onProceedWithWalletBalance(amount: Double, tips: Double){

        viewModelScope.launch {

            state.value.isLoading = true
            performGoodsAndServiceTransaction(
                amount = amount,
                tips = tips,
            ).fold(
                onSuccess = {
                    state.value.onNext = true
                    state.value.isLoading = false
                    clearTransactionCache()
                },
                onFailure = {

                },
            )
        }

    }

    fun onNavigationConsumed() {
        state.value.onNext = false
    }
}

package com.ledgergreen.terminal.ui.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.ui.agreement.domain.GetAgreementUseCase
import com.ledgergreen.terminal.ui.agreement.domain.SaveSignatureUseCase
import com.ledgergreen.terminal.ui.card.details.CardDetailsViewModel
import com.ledgergreen.terminal.ui.card.details.domain.PerformGoodsAndServiceTransactionUseCase
import com.ledgergreen.terminal.ui.receipt.ReceiptState
import com.ledgergreen.terminal.ui.tips.domain.GetAmountUseCase
import com.ledgergreen.terminal.ui.tips.domain.GetCustomerWalletDetailsUseCase
import com.ledgergreen.terminal.ui.tips.domain.GetTipsOptionsUseCase
import com.ledgergreen.terminal.ui.tips.domain.SetTipOptionUseCase
import com.ledgergreen.terminal.ui.tips.domain.TipOption
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class TipsViewModel @Inject constructor(
    getAmount: GetAmountUseCase,
    getCustWallet: GetCustomerWalletDetailsUseCase,
    getTipsOptions: GetTipsOptionsUseCase,
    private val setTips: SetTipOptionUseCase,
    private val saveSignature: SaveSignatureUseCase,
    private val getAgreement: GetAgreementUseCase,
    private val transactionCache: TransactionCache,
    private val performGoodsAndServiceTransaction: PerformGoodsAndServiceTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        TipsState(
            orderAmount = getAmount().toCurrencyString(),
            tipsOptions = getTipsOptions().toImmutableList(),
            selectedTipOption = null,
            tipCustom = "",
            navigateNext = false,
            navigateNextToHomeScreen = false,
            eventSink = ::eventSink,
            walletBalance = getCustWallet().amount,
            accountNumber = getCustWallet().accountNumber,
            agreementText = "",
            signature = null,
            agreementAccepted = true,
            customerExtId = transactionCache.custWalletResponse.value!!.customerExtId,
            receiptId = "",
            orderAmountAfterTransaction = "",
            error = null,
            errorMsg = null,
            showTipAmount = transactionCache.pinResponse.value!!.store.showTipAmount,
        ),
    )


    val state: StateFlow<TipsState> get() = _state

    init {
        viewModelScope.launch {
            getAgreement().collect {
                _state.value = state.value.copy(agreementText = it)
            }
        }
    }

    private fun eventSink(event: TipEvent) {
        when (event) {
            is TipEvent.CustomTipChanged -> onCustomTipChange(event.amount)
            is TipEvent.TipOptionSelected -> onTipChange(event.tipOption)
        }
    }

    private fun onTipChange(tipOption: TipOption) {
        _state.value = _state.value.copy(
            selectedTipOption = tipOption,
            tipCustom = "",
        )
    }

    fun onSigned(signature: Signature?) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _state.value.signature?.recycle()

                _state.value = _state.value.copy(
                    signature = signature,
                )
            }
        }
    }

    private fun onCustomTipChange(customTip: String) {
        _state.value = _state.value.copy(
            selectedTipOption = null,
            tipCustom = customTip,
        )
    }

    fun onProceed() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateTipsAmount()
                _state.value = state.value.copy(
                    navigateNext = true,
                )
            }
        }
    }

    fun onProceedWithWalletBalance(amount: Double, tips: Double){

        viewModelScope.launch {
            val state = state.value

            performGoodsAndServiceTransaction(
                amount = amount,
                tips = tips,
            ).fold(
                onSuccess = {
                    _state.value = state.copy(
//                        loading = false,
//                        transactionError = null,
                        navigateNextToHomeScreen = true,
                        receiptId = transactionCache.transactionGoodsAndServiceResponse.value!!.order_id,
                        orderAmountAfterTransaction = transactionCache.transactionGoodsAndServiceResponse.value!!.amount.toString()
                    )
                    transactionCache.clearTransactionResponse()
                },
                onFailure = {
                    _state.value = state.copy(
//                        isLoading = false,
                        error = AppState1.balanceUpdate,
                        errorMsg = it.message
//                        success = false
                    )
                },
            )
        }

    }

    fun onProceedWithSignature() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val signature = state.value.signature!!
                saveSignature(signature)
                _state.value = state.value.copy(
                    navigateNext = true,
                )
            }
        }
    }

    private fun updateTipsAmount() {
        val state = state.value
        val tipOption = if (state.selectedTipOption != null) {
            state.selectedTipOption
        } else {
            val customTips = state.tipCustom.toDoubleOrNull()?.let { it / 100.0 } ?: 0.0
            TipOption.FlatTipOption(customTips.toMoney(), enteredManually = true)
        }

        setTips(tipOption)
    }

    fun onNavigationConsumed() {
        _state.value = state.value.copy(
            navigateNext = false,
            error = null
        )
    }

    override fun onCleared() {
        super.onCleared()

    }

    fun clearError(){
        _state.value = state.value.copy(
            errorMsg = null,
            error = null
        )
    }

    fun hasTipsOptions() = transactionCache.pinResponse.value?.tips?.isNotEmpty() == true
}

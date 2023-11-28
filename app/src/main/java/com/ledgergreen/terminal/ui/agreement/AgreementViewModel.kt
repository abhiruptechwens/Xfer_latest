package com.ledgergreen.terminal.ui.agreement

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
import com.ledgergreen.terminal.ui.amount.domain.SaveAmountUseCase
import com.ledgergreen.terminal.ui.card.details.domain.GetTransactionInputUseCase
import com.ledgergreen.terminal.ui.card.details.domain.PerformTransactionUseCase
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.failure.isNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AgreementViewModel @Inject constructor(
    private val saveSignature: SaveSignatureUseCase,
    private val getAgreement: GetAgreementUseCase,
    private val setAmount: SaveAmountUseCase,
    private val performTransaction: PerformTransactionUseCase,
    private val getCardDetailsInput: GetTransactionInputUseCase,
    private val transactionCache: TransactionCache,
) : ViewModel() {

    private val _state = MutableStateFlow(
        AgreementState(
            agreementText = "",
            signature = null,
            agreementAccepted = true,
            navigateNext = false,
        ),
    )

    val state: StateFlow<AgreementState> get() = _state

    init {
        viewModelScope.launch {
            getAgreement().collect {
                _state.value = state.value.copy(agreementText = it)
            }
        }
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

    fun onProceed() {
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

//    fun onProceedWithSavedSignature(amount :String) {
//
//        val amountLocal = if (amount.contains("$"))
//            amount.drop(1)
//        else
//            amount
//
//        viewModelScope.launch {
//            withContext(Dispatchers.Default) {
////                val signature = state.value.signature!!
////                saveSignature(signature)
//                val amountMoney = amount.toMoney()
//                amountMoney?.let { setAmount(it) }
//                _state.value = state.value.copy(
//                    navigateNext = true,
//                )
//
//                amount!!.toMoney()?.let {
//                    performTransaction(
//                        card = AppState1.cardDetails!!,
//                        amount = it,
//                        tips = AppState1.tips.toMoney(),
//                        tipsType = "",
//                        cardType = AppState1.cardType,
//                        cardFee = AppState1.cardFee!!,
//                        amountPlusFee = AppState1.amountPlusFee!!.toDouble(),
//                        cardToken = ""
//                    ).fold(
//                        onSuccess = {
//                            _state.value = state.value.copy(
//                                navigateNext = true,
//                            )
//                        },
//                        onFailure = {
//
//                        },
//                    )
//                }
//            }
//        }
//    }

    fun onNavigationConsume() {
        _state.value = state.value.copy(
            navigateNext = false,
        )
    }
}

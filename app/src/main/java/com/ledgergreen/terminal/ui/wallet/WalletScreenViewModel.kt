package com.ledgergreen.terminal.ui.wallet

import com.ledgergreen.terminal.ui.pin.PinState


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.brand.BrandManager
import com.ledgergreen.terminal.data.model.Pin
import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.data.network.model.CustResponse
import com.ledgergreen.terminal.domain.GetVersionInfoUseCase
import com.ledgergreen.terminal.domain.customer.FindCustomerByCustomerIdResult
import com.ledgergreen.terminal.domain.customer.FindCustomerByCustomerIdUseCase
import com.ledgergreen.terminal.domain.pin.EnterPinUseCase
import com.ledgergreen.terminal.domain.scan.FindCustomerByDocumentResult
import com.ledgergreen.terminal.domain.scan.FindCustomerByDocumentResult.*
import com.ledgergreen.terminal.ui.receipt.ReceiptState
import com.ledgergreen.terminal.ui.tips.TipsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class WalletScreenViewModel @Inject constructor(
    private val customerId: FindCustomerByCustomerIdUseCase,
    private val transactionCache: TransactionCache,
    getVersionInfo: GetVersionInfoUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(
        WalletState(
            customerId = "",
            isLoading = false,
            appInfo = getVersionInfo(),
            error = null,
            success = false,
            custResponse = null,
            onErrorShown = this::onErrorShown,
            setPage = this::setFromPage
        ),
    )

    val state: StateFlow<WalletState> get() = _state

    fun onGetCustomer(custID: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                isLoading = true,
                error = null,
            )

            customerId(custID)
                .fold(
                    onSuccess = {findCustomer ->
                        when(findCustomer){
                            is FindCustomerByCustomerIdResult.CustomerFound ->{
//                                AppState1.savedCards = findCustomer.customerResponse.card
                                AppState1.customerExtId = findCustomer.customerResponse.customerExtId
                                _state.update {
                                    it.copy(
                                        custResponse = findCustomer.customerResponse,
                                        isLoading = false,
                                        success = true,
                                        error = null,
                                    )
                                }
                            }

                            else -> {}
                        }
                    },
                    onFailure = {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = it.message,
                            success = false
                        )
                    },
                )
        }
    }

    fun onErrorShown() {
        _state.value = state.value.copy(
            error = null,
        )
    }

    fun setFromPage(from:String){
        transactionCache.setFromPage(from)
    }
}

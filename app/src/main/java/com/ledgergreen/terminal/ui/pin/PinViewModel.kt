package com.ledgergreen.terminal.ui.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.brand.BrandManager
import com.ledgergreen.terminal.data.local.SessionManager
import com.ledgergreen.terminal.data.model.Pin
import com.ledgergreen.terminal.domain.GetVersionInfoUseCase
import com.ledgergreen.terminal.domain.pin.EnterPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class PinViewModel @Inject constructor(
    private val enterPin: EnterPinUseCase,
    getVersionInfo: GetVersionInfoUseCase,
    private val transactionCache: TransactionCache,
    sessionManager: SessionManager,

    brandManager: BrandManager,
) : ViewModel() {

    private val _state = MutableStateFlow(
        PinState(
            vendorName = "",
            pinCode = "",
            isLoading = false,
            appInfo = getVersionInfo(),
            error = null,
            success = false,
            onErrorShown = this::onErrorShown,
//            terminal = transactionCache.loginResponse.value?.terminal,
            terminal = null,
            companyId = null,
//            terminal = null,
//            storeName = transactionCache.loginResponse.value?.name
//            companyId = transactionCache.loginResponse.value?.companyId
        ),
    )

    val state: StateFlow<PinState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
//
            sessionManager.getLoginResponse().filterNotNull().collect{
                _state.value = state.value.copy(
                    terminal = it.terminal,
                    companyId = it.companyId
                )
            }

        }

//        viewModelScope.launch {
//
//            brandManager.getBrand()
//                .filterNotNull()
//                .collect {
//                    _state.value = state.value.copy(
//                        vendorName = it.vendorName,
//                    )
//                }
//
//        }
    }

    fun onPinChanged(pinCode: String, finish: Boolean) {
        _state.value = state.value.copy(
            pinCode = pinCode,
        )

        if (finish) {
            onConfirm()
        }
    }

    private fun onConfirm() {
        Timber.d("onConfirm PIN")
        viewModelScope.launch {
            val currentState = state.value

            _state.value = currentState.copy(
                isLoading = true,
            )

            val pin = Pin(state.value.pinCode)
            enterPin(pin).fold(
                onSuccess = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        error = null,
                        success = true,
                    )
                },
                onFailure = {
                    _state.value = currentState.copy(
                        pinCode = "",
                        isLoading = false,
                        error = it.message ?: "Unknown error",
                        success = false,
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
}

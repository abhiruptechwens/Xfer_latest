package com.ledgergreen.terminal.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.data.AccountRepository
import com.ledgergreen.terminal.data.brand.BrandManager
import com.ledgergreen.terminal.data.model.AuthenticationState
import com.ledgergreen.terminal.data.model.CardDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.WalletDetails
import com.ledgergreen.terminal.domain.GetAuthStateUseCase
import com.ledgergreen.terminal.domain.InitializePosAccessoryUseCase
import com.ledgergreen.terminal.domain.SwitchPinUseCase
import com.ledgergreen.terminal.idle.IdleLocker
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppStateViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    brandManager: BrandManager,
    getAuthStateUseCase: GetAuthStateUseCase,
    private val switchPin: SwitchPinUseCase,
    private val idleLocker: IdleLocker,
    private val initPosAccessory: InitializePosAccessoryUseCase,
    private val analytics: Analytics,
) : ViewModel() {

    val lockState: StateFlow<AppLockState>
        get() = idleLocker.autolockCountDownMs.map {
            AppLockState(timeToAutolockMs = it)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AppLockState(),
        )

    val appState: StateFlow<AppState> = combine(
        getAuthStateUseCase(),
        flow {
            initPosAccessory()
            emit(true)
        },
        brandManager.getBrand(),
    ) { authState, _, brand ->
        AppState(authState, brand)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppState(
            authenticationState = AuthenticationState.INITIAL,
            brand = null,
        ),
    )

    init {
        viewModelScope.launch(Dispatchers.Main) {
            appState
                .flatMapLatest {
                    if (it.authenticationState == AuthenticationState.AUTHENTICATED) {
                        idleLocker.activate()
                    } else {
                        flowOf(false)
                    }
                }
                .collect { switchPin ->
                    if (switchPin) {
                        Timber.d("Exit by idle time")
                        switchPin(isIdleLock = true)
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            accountRepository.logout()
            AppState1.balanceAmount = null
            AppState1.totalAmount = null
            AppState1.cardFee = null
            AppState1.amountPlusFee = null
            AppState1.cardType = ""
            AppState1.inputAmount = ""
            AppState1.cardDetails = null
            AppState1.customerResponse = null
            AppState1.walletDetails = null
            AppState1.tips = 0.0
            AppState1.customerExtId = ""
            PageState.fromPage = ""
        }
    }

    fun dropPin() {
        switchPin(isIdleLock = false)
        AppState1.balanceAmount = null
        AppState1.totalAmount = null
        AppState1.cardFee = null
        AppState1.amountPlusFee = null
        AppState1.cardType = ""
        AppState1.inputAmount = ""
        AppState1.cardDetails = null
        AppState1.customerResponse = null
        AppState1.walletDetails = null
        AppState1.tips = 0.0
        AppState1.customerExtId = ""
        PageState.fromPage = ""
    }

    fun onIdleLockInterrupted() {
        idleLocker.onActivityDetected()
        analytics.trackCustomAction(Actions.idleLockInterrupted)
    }
}

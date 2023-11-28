package com.ledgergreen.terminal.ui.home

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.model.UserInfo
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.WalletDetails
import com.ledgergreen.terminal.domain.ClearTransactionUseCase
import com.ledgergreen.terminal.domain.GetUserInfoUseCase
import com.ledgergreen.terminal.domain.scan.FindCustomerByDocumentResult
import com.ledgergreen.terminal.domain.scan.FindCustomerByDocumentUseCase
import com.ledgergreen.terminal.domain.scan.ProceedPurchaseWithCustomerUseCase
import com.ledgergreen.terminal.domain.scan.SaveScannedDocumentUseCase
import com.ledgergreen.terminal.scanner.Document
import com.ledgergreen.terminal.scanner.IdScanner
import com.ledgergreen.terminal.scanner.ScannerMethod
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scanner: IdScanner,
    private val findCustomer: FindCustomerByDocumentUseCase,
    private val saveScannedDocument: SaveScannedDocumentUseCase,
    private val proceedPurchaseWithCustomer: ProceedPurchaseWithCustomerUseCase,
    private val clearTransaction: ClearTransactionUseCase,
    private val getUserInfo: GetUserInfoUseCase,
    private val stringResources: StringResources,
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(
            userInfo = UserInfo("", ""),
            loading = false,
            registrationRequired = false,
            customerResponse = null,
            walletResponse = null,
            scannerStarted = false,
            showScannerHint = scanner.scannerMethod == ScannerMethod.Camera,
            errorMessage = null,
            onErrorShown = ::onErrorShown,
            navigateNext = false,
            isDialogShown = false,
        ),
    )

    val state: StateFlow<HomeState> get() = _state

    private var scanJob: Job? = null

    init {
        viewModelScope.launch {
            getUserInfo().collect { userInfo ->
                _state.update { it.copy(userInfo = userInfo) }
            }
        }
    }

    fun onDocScan() {



        scanJob?.takeIf { it.isActive }?.cancel()

        scanJob = viewModelScope.launch {
            runCatching {
                clearTransaction()
                clearScan()

                _state.update {
                    it.copy(
                        loading = false,
                        registrationRequired = false,
                        customerResponse = null,
                        scannerStarted = true,
                    )
                }


                scanner.scan()
            }.fold(
                onSuccess = { document ->
                    _state.update {
                        it.copy(scannerStarted = false)
                    }
                    document?.let { onScanned(it) }
                },
                onFailure = { exception ->
                    _state.update { it.copy(scannerStarted = false) }
                    if (exception !is CancellationException) {
                        Timber.w(exception, "DocScan exception")
                        _state.update {
                            it.copy(
                                errorMessage = exception.message
                                    ?: stringResources.getString(R.string.error_scanning),
                                loading = false
                            )
                        }
                    }
                },
            )
        }
    }

    fun onErrorShown() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }

    fun clearScan() {
        _state.update {
            it.copy(
                scannerStarted = false,
                customerResponse = it.customerResponse,
                registrationRequired = false,
                errorMessage = null,
                navigateNext = false,
            )
        }
    }

    private suspend fun onScanned(document: Document) {
        clearTransaction()
        _state.update {
            it.copy(loading = true)
        }

        findCustomer(document).fold(
            onSuccess = { findCustomerResult ->
                when (findCustomerResult) {
                    is FindCustomerByDocumentResult.CustomerFound -> {
                        Timber.d("Customer found")
                        _state.update {
                            it.copy(
                                customerResponse = findCustomerResult.customerResponse,
                                registrationRequired = false,
                                loading = false,
                                isDialogShown = true,
                                errorMessage = null,
                            )
                        }
                    }

                    FindCustomerByDocumentResult.CustomerNotFound -> {
                        Timber.d("Customer not found. Registration required")

                        val docuDate = LocalDate.parse(document.expiryDate.toString())
                        val currentDate = LocalDate.now()
                        saveScannedDocument(document)
                        if(docuDate.isAfter(currentDate)) {
                            _state.update {
                                it.copy(
                                    customerResponse = null,
                                    registrationRequired = true,
                                    loading = false,
                                    isDialogShown = false,
                                    errorMessage = null,
                                )
                            }
                        }
                        else{
                            _state.update {
                                it.copy(
                                    customerResponse = null,
                                    registrationRequired = false,
                                    loading = false,
                                    isDialogShown = false,
                                    errorMessage = "Date has expired!",
                                )
                            }
                        }
                    }
                }
            },
            onFailure = { exception ->
                _state.update {
                    it.copy(
                        errorMessage = exception.displayableErrorMessage(stringResources),
                        walletResponse = null,
                        isDialogShown = false,
                        loading = false
                    )
                }
            },
        )
    }

    private fun confirmCustomerAndProceed(customerResponse: CustomerResponse) {
        proceedPurchaseWithCustomer(customerResponse)

        AppState1.customerResponse = customerResponse
        _state.update {
            it.copy(
                scannerStarted = false,
                registrationRequired = false,
                customerResponse = customerResponse,
                navigateNext = true,
                isDialogShown = false,
            )
        }
    }

    fun onConfirmCustomer() {
        state.value.customerResponse?.let {
                confirmCustomerAndProceed(it)

        } ?: error("Unable to confirm customer. customerResponse is null")

        state.value.walletResponse?.let {
            AppState1.walletDetails = it
        }
    }

    fun onNavigateNextConsumed() {
        _state.update {
            it.copy(navigateNext = false)
        }
    }

    fun onScanCancel() {
        _state.update {
            it.copy(
                customerResponse = null,
                scannerStarted = false,
                loading = false,
            )
        }
        scanJob?.cancel(CancellationException("Scanner canceled"))
    }

    fun onNavigateRegisterConsumed() {
        _state.update {
            it.copy(registrationRequired = false)
        }
    }
}

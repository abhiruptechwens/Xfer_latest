package com.ledgergreen.terminal.ui.home.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.data.network.model.CustomerIdType
import com.ledgergreen.terminal.domain.ClearTransactionUseCase
import com.ledgergreen.terminal.domain.scan.CustomerRegistrationForm
import com.ledgergreen.terminal.domain.scan.GetScannedDocumentUseCase
import com.ledgergreen.terminal.domain.scan.RegisterNewCustomerUseCase
import com.ledgergreen.terminal.scanner.Document
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class RegisterCustomerViewModel @Inject constructor(
    private val registerCustomer: RegisterNewCustomerUseCase,
    getScannedDocumentUseCase: GetScannedDocumentUseCase,
    private val stringResources: StringResources,
    private val clearTransaction: ClearTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        RegisterCustomerState(
            form = getScannedDocumentUseCase()?.toCustomerRegistrationForm()
                ?: error("Unable to register user without document scan"),
            loading = false,
            error = null,
            onErrorShown = ::onErrorShown,
            registeredCustomerResponse = null,
        ),
    )

    val state: StateFlow<RegisterCustomerState> = _state

    fun onFormChanged(customerRegistrationForm: CustomerRegistrationForm) {
        _state.update { it.copy(form = customerRegistrationForm) }
    }

    fun onCancelRegistration() {
        clearTransaction()
    }

    fun onRegister() {
        viewModelScope.launch {
            _state.update {
                it.copy(loading = true)
            }

            val form = state.value.form
            registerCustomer(form).fold(
                onSuccess = { customerResponse ->
                    Timber.i("Customer registered successfully")
                    _state.update {
                        it.copy(
                            error = null,
                            loading = false,
                            registeredCustomerResponse = customerResponse,
                        )
                    }
                },
                onFailure = { throwable ->
                    Timber.i(throwable, "Customer registration failed")
                    _state.update {
                        it.copy(
                            error = throwable.displayableErrorMessage(stringResources),
                            loading = false,
                            registeredCustomerResponse = null,
                        )
                    }
                },
            )
        }
    }

    fun onErrorShown() {
        _state.value = state.value.copy(error = null)
    }

    private fun Document.toCustomerRegistrationForm(): CustomerRegistrationForm =
        when (this) {
            is Document.DriverLicense -> CustomerRegistrationForm(
                idNumber = this.documentNumber,
                idType = CustomerIdType.DriverLicense,
                firstName = this.firstName,
                lastName = this.lastName,
                middleName = this.middleName,
                birthdate = this.birthDate,
                expirationDate = this.expiryDate,
                gender = this.sex,
                country = this.country,
                address1 = this.address1,
                city = this.city,
                state = this.state,
                issueDate = this.issueDate,
                postalCode = this.postalCode,
                phoneNumber = null,
            )

            is Document.MrzDocument -> CustomerRegistrationForm(
                idNumber = this.documentNumber,
                idType = CustomerIdType.Document,
                firstName = this.firstName,
                lastName = this.lastName,
                middleName = null,
                birthdate = this.birthDate,
                expirationDate = this.expiryDate,
                gender = this.sex,
                country = this.country,
                address1 = null,
                city = null,
                state = null,
                issueDate = null,
                postalCode = null,
                phoneNumber = null,
            )
        }
}

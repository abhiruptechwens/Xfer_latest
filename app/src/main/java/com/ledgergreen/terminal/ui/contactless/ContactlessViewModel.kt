package com.ledgergreen.terminal.ui.contactless

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.domain.ClearTransactionUseCase
import com.ledgergreen.terminal.domain.ContactlessPaymentInput
import com.ledgergreen.terminal.domain.CreateContactlessPaymentUseCase
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.ui.tips.domain.GetAmountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ContactlessViewModel @Inject constructor(
    private val createContactlessPayment: CreateContactlessPaymentUseCase,
    getAmountUseCase: GetAmountUseCase,
    private val clearTransactionUseCase: ClearTransactionUseCase,
    private val analytics: Analytics,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ContactlessState(
            phoneNumber = PhoneNumber.default,
            customerName = "",
            amount = getAmountUseCase(),
            formValid = false,
            sendSms = true,
            loading = false,
            contactlessPayment = null,
            error = null,
            onErrorShown = ::onErrorShown,
            navigateNext = false,

        ),
    )

    val state: StateFlow<ContactlessState> = _state

    fun onPhoneChanged(phone: String) {
        val phoneNumber = state.value.phoneNumber.copy(phone = phone)

        _state.value = state.value.copy(
            phoneNumber = phoneNumber,
            contactlessPayment = null,
            formValid = isFormValid(phoneNumber = phoneNumber),
        )
    }

    fun onCountryCodeChanged(code: CountryPhoneCode) {
        val phoneNumber = state.value.phoneNumber.copy(countryCode = code)

        _state.value = state.value.copy(
            phoneNumber = phoneNumber,
            contactlessPayment = null,
            formValid = isFormValid(phoneNumber = phoneNumber),
        )
    }

    fun onCustomerNameChanged(name: String) {
        _state.value = state.value.copy(
            customerName = name,
            contactlessPayment = null,
            formValid = isFormValid(customerName = name),
        )
    }

    fun onSendSms() {
        internalCreateContactlessPayment(sendSms = true)
    }

    fun onGenerateQrCode() {
        internalCreateContactlessPayment(sendSms = false)
    }

    private fun internalCreateContactlessPayment(sendSms: Boolean) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true,
                contactlessPayment = null,
            )

            val currentState = state.value
            createContactlessPayment(
                ContactlessPaymentInput(
                    phoneNumber = currentState.phoneNumber,
                    customerName = currentState.customerName,
                    amount = currentState.amount,
                    sendSms = sendSms,
                ),
            ).fold(
                onSuccess = { contactlessPaymentResult ->
                    Timber.i("Contactless payment created")

                    val contactlessType = if (contactlessPaymentResult.smsSent) "SMS" else "QR code"

                    _state.value = state.value.copy(
                        loading = false,
                        contactlessPayment = contactlessPaymentResult,
                    )

                    analytics.trackCustomAction(
                        name = Actions.contactlessPaymentCreated,
                        attributes = mapOf("contactless_type" to contactlessType),
                    )
                },
                onFailure = {
                    _state.value = state.value.copy(
                        error = it.message,
                        loading = false,
                    )
                },
            )
        }
    }

    private fun isFormValid(
        phoneNumber: PhoneNumber = state.value.phoneNumber,
        customerName: String = state.value.customerName,
    ): Boolean {
        val phoneSet = phoneNumber.isValidPhoneLength()
        val customerNameSet = customerName.isNotBlank()
        return phoneSet && customerNameSet
    }

    fun onErrorShown() {
        _state.value = state.value.copy(
            error = null,
        )
    }

    fun clearTransaction() {
        clearTransactionUseCase()
        state.value.copy(
            contactlessPayment = null
        )
    }

    fun onNavigateConsumed() {
        _state.value = state.value.copy(
            navigateNext = false,
        )
    }
}

package com.ledgergreen.terminal.ui.home.register

import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.domain.scan.CustomerRegistrationForm

data class RegisterCustomerState(
    val form: CustomerRegistrationForm,
    val loading: Boolean,
    val error: String? = null,
    val onErrorShown: () -> Unit,
    val registeredCustomerResponse: CustomerResponse?,
)

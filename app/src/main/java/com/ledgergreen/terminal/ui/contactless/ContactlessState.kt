package com.ledgergreen.terminal.ui.contactless

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.domain.ContactlessPayment

@Stable
@Immutable
data class ContactlessState(
    val phoneNumber: PhoneNumber,
    val customerName: String,
    val amount: Money,
    val sendSms: Boolean,
    val formValid: Boolean,
    val loading: Boolean,
    val contactlessPayment: ContactlessPayment?,
    val error: String?,
    val onErrorShown: () -> Unit,
    val navigateNext : Boolean,
)

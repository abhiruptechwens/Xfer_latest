package com.ledgergreen.terminal.ui.common.phonecountryselectiondialog

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode

@Stable
@Immutable
data class SelectCountryCodeState(val countryCodes: List<CountryPhoneCode>)

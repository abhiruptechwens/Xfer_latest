package com.ledgergreen.terminal.ui.common.phonecountryselectiondialog.domain

import com.ledgergreen.terminal.data.model.phone.CountryCodes
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import javax.inject.Inject

class GetCountryPhoneCodesUseCase @Inject constructor() {
    operator fun invoke(): List<CountryPhoneCode> = CountryCodes.all;
}

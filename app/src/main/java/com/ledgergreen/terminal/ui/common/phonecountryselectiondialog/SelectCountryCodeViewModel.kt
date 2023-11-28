package com.ledgergreen.terminal.ui.common.phonecountryselectiondialog

import androidx.lifecycle.ViewModel
import com.ledgergreen.terminal.ui.common.phonecountryselectiondialog.domain.GetCountryPhoneCodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SelectCountryCodeViewModel @Inject constructor(
    getCountryPhoneCodesUseCase: GetCountryPhoneCodesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SelectCountryCodeState(
            countryCodes = getCountryPhoneCodesUseCase(),
        ),
    )

    val state: StateFlow<SelectCountryCodeState> get() = _state
}

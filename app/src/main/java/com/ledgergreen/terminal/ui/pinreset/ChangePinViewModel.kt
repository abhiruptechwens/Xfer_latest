package com.ledgergreen.terminal.ui.pinreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.domain.GetVersionInfoUseCase
import com.ledgergreen.terminal.domain.pin.ChangePinUseCase
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChangePinViewModel @Inject constructor(
    private val changePin: ChangePinUseCase,
    getVersionInfo: GetVersionInfoUseCase,
    private val stringResources: StringResources,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChangePinState(
            pin1 = "",
            pin2 = "",
            loading = false,
            error = null,
            versionInfo = getVersionInfo(),
            eventSink = ::eventSink,
        ),
    )

    val state: StateFlow<ChangePinState> = _state

    private fun eventSink(event: ChangePinEvent) {
        when (event) {
            is ChangePinEvent.Pin1Changed -> onPin1Change(event.pin)
            is ChangePinEvent.Pin2Changed -> onPin2Change(event.pin)
            ChangePinEvent.ChangePin -> onChangePin()
            ChangePinEvent.ErrorShown -> onErrorShown()
        }
    }

    private fun onChangePin() {
        val pin1 = state.value.pin1
        val pin2 = state.value.pin2
        if (pin1 != pin2) {
            _state.value = state.value.copy(
                pin2 = "",
                error = stringResources.getString(R.string.error_pin_does_not_match),
            )
        } else {
            _state.value = state.value.copy(
                loading = true,
            )
            viewModelScope.launch {
                changePin(pin1).fold(
                    onSuccess = {
                        _state.value = state.value.copy(
                            loading = false,
                        )
                    },
                    onFailure = {
                        _state.value = state.value.copy(
                            loading = false,
                            pin1 = "",
                            pin2 = "",
                            error = it.displayableErrorMessage(stringResources),
                        )
                    },
                )
            }
        }
    }

    private fun onPin1Change(pinCode: String) {
        _state.value = state.value.copy(
            pin1 = pinCode,
        )
    }

    private fun onPin2Change(pinCode: String) {
        _state.value = state.value.copy(
            pin2 = pinCode,
        )
    }

    private fun onErrorShown() {
        _state.value = state.value.copy(
            error = null,
        )
    }
}

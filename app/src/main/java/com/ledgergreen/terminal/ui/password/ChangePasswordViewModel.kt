package com.ledgergreen.terminal.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.domain.password.ResetPasswordUseCase
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePassword: ResetPasswordUseCase,
    private val stringResources: StringResources,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChangePasswordState(
            password1 = "",
            password2 = "",
            loading = false,
            errorMessage = null,
            fieldValidationError = null,
            eventSink = ::eventSink,
        ),
    )

    val state: StateFlow<ChangePasswordState> = _state

    private fun eventSink(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.Password1Change -> onPassword1Change(event.password)
            is ChangePasswordEvent.Password2Change -> onPassword2Change(event.password)
            ChangePasswordEvent.ChangePassword -> onChangePassword()
            ChangePasswordEvent.OnErrorShown -> onErrorShown()
        }
    }

    private fun onErrorShown() {
        _state.value = state.value.copy(
            errorMessage = null,
        )
    }

    private fun onChangePassword() {
        Timber.i("Change password")
        val state = _state.value

        validatePasswords(state.password1, state.password2)?.let {
            _state.value = _state.value.copy(fieldValidationError = it)
            return
        }

        viewModelScope.launch {
            if (state.password1 == state.password2) {
                _state.value = _state.value.copy(
                    loading = true,
                )
                changePassword(state.password1).fold(
                    onSuccess = {
                        _state.value = _state.value.copy(
                            loading = false,
                        )
                    },
                    onFailure = {
                        _state.value = _state.value.copy(
                            loading = false,
                            errorMessage = it.message,
                        )
                    },
                )
            }
        }
    }

    private fun onPassword1Change(password: String) {
        _state.value = _state.value.copy(
            password1 = password,
            fieldValidationError = null,
        )
    }

    private fun onPassword2Change(password: String) {
        _state.value = _state.value.copy(
            password2 = password,
            fieldValidationError = null,
        )
    }

    private fun validatePasswords(password1: String, password2: String): String? = when {
        password1.length < 8 -> stringResources.getString(R.string.validation_password_at_least_8_symbols)
        password1 != password2 -> stringResources.getString(R.string.validation_passwords_are_matching)
        else -> null
    }
}

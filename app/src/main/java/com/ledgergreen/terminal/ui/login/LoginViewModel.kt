package com.ledgergreen.terminal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginState(
            login = "",
            password = "",
            isLoading = false,
            error = null,
            onErrorShown = ::onErrorShown,
            success = false,
        )
    )

    val state: StateFlow<LoginState> get() = _state

    fun onLoginChange(login: String) {
        _state.value = state.value.copy(
            login = login,
        )
    }

    fun onPasswordChange(password: String) {
        _state.value = state.value.copy(
            password = password,
        )
    }

    fun onLogin() {
        viewModelScope.launch {
            val currentState = state.value
            _state.value = state.value.copy(
                isLoading = true,
                error = null,
            )

            loginUseCase(currentState.login, currentState.password)
                .fold(
                    onSuccess = {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = null,
                            success = true,
                        )
                    },
                    onFailure = {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = it.message,
                            success = false
                        )
                    }
                )
        }
    }

    fun onErrorShown() {
        _state.value = state.value.copy(
            error = null,
        )
    }
}

package com.ledgergreen.terminal.domain

import com.auth0.android.jwt.JWT
import com.datadog.android.Datadog
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.local.SessionManager
import com.ledgergreen.terminal.data.model.AuthenticationState
import com.ledgergreen.terminal.data.network.model.LoginResponse
import com.ledgergreen.terminal.data.network.model.PinResponse
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetAuthStateUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): Flow<AuthenticationState> = combine(
        sessionManager.getLoginResponse(),
        transactionCache.pinResponse,
    ) { loginResponse, pinResponse ->
        val newState = if (loginResponse == null) {
            AuthenticationState.LOGIN_REQUIRED
        } else {
            val isExpired = JWT(loginResponse.token).isExpired(10)
            if (isExpired) {
                AuthenticationState.LOGIN_REQUIRED
            } else {
                if (loginResponse.resetPasswordRequired) {
                    AuthenticationState.PASSWORD_RESET_REQUIRED
                } else {
                    if (pinResponse == null) {
                        withContext(Dispatchers.IO) {
                            setDatadogCompanyInfo(loginResponse)
                        }
                        AuthenticationState.PIN_REQUIRED
                    } else {
                        withContext(Dispatchers.IO) {
                            setDatadogFullInfo(loginResponse, pinResponse)
                        }
                        if (pinResponse.pinResetRequired) {
                            AuthenticationState.PIN_RESET_REQUIRED
                        } else {
                            AuthenticationState.AUTHENTICATED
                        }
                    }
                }
            }
        }
        Timber.d("AuthState: $newState")
        newState
    }

    private fun setDatadogCompanyInfo(loginResponse: LoginResponse) {
        Datadog.setUserInfo(
            id = null,
            name = null,
            extraInfo = mapOf(
                "terminal_name" to loginResponse.name,
                "company_id" to loginResponse.companyId,
                "vendor" to loginResponse.brand.vendorName,
            ),
        )
    }

    private fun setDatadogFullInfo(loginResponse: LoginResponse, pinResponse: PinResponse) {
        Datadog.setUserInfo(
            id = pinResponse.userId.toString(),
            name = pinResponse.name,
            extraInfo = mapOf(
                "terminal_name" to loginResponse.name,
                "company_id" to loginResponse.companyId,
                "vendor" to loginResponse.brand.vendorName,
            ),
        )
    }
}

package com.ledgergreen.terminal.data

import com.ledgergreen.terminal.data.local.SessionManager
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber


interface AccountRepository {

    suspend fun login(login: String, password: String)

    suspend fun logout()

    fun getToken(): String?

    suspend fun markPasswordReset()
}

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : AccountRepository {

    override suspend fun login(login: String, password: String) = withContext(Dispatchers.IO) {
        val baseLoginResponse = apiService.login(login, password)
        if (baseLoginResponse.status) {
            val loginResponse = baseLoginResponse.response!!
            sessionManager.saveLoginResponse(loginResponse)
            Timber.d("Login success")
        } else {
            throw RequestException(baseLoginResponse.message)
        }
    }

    override suspend fun logout() {
        sessionManager.removeLogin()
    }

    override fun getToken(): String? = runBlocking {
        sessionManager.getLoginResponse().firstOrNull()?.token
    }

    override suspend fun markPasswordReset() {
        // modify resetPasswordRequired flag and save
        sessionManager.getLoginResponse().first()?.copy(
            resetPasswordRequired = false,
        )?.let { sessionManager.saveLoginResponse(it) }
    }
}

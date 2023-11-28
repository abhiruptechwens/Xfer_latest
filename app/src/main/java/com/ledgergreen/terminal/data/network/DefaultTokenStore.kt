package com.ledgergreen.terminal.data.network

import com.ledgergreen.terminal.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTokenStore @Inject constructor(
    private val sessionManager: SessionManager,
) : TokenStore {
    override fun getToken(): String? = runBlocking {
        sessionManager.getLoginResponse().firstOrNull()?.token
    }

    override fun invalidate() = runBlocking { sessionManager.removeLogin() }
}

package com.ledgergreen.terminal.data.brand

import com.ledgergreen.terminal.data.local.SessionManager
import com.ledgergreen.terminal.data.network.model.Brand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BrandManager @Inject constructor(
    private val sessionManager: SessionManager,
) {

    fun getBrand(): Flow<Brand?> = sessionManager.getLoginResponse()
        .map { it?.brand }
}

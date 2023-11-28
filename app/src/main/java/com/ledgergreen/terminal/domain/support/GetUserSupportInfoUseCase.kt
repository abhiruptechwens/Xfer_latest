package com.ledgergreen.terminal.domain.support

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.local.SessionManager
import com.ledgergreen.terminal.domain.GetVersionInfoUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

data class SupportInfo(
    val versionInfo: String,
    val storeId: Long?,
    val storeName: String?,
    val userId: Long?,
    val username: String?,
)

class GetUserSupportInfoUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val transactionCache: TransactionCache,
    private val getVersionInfo: GetVersionInfoUseCase,
) {
    operator fun invoke(): Flow<SupportInfo> = combine(
        flowOf(getVersionInfo()),
        sessionManager.getLoginResponse(),
        transactionCache.pinResponse,
    ) { versionInfo, loginResponse, pinResponse ->
        SupportInfo(
            versionInfo = versionInfo,
            storeId = loginResponse?.companyId,
            storeName = loginResponse?.name,
            userId = pinResponse?.userId,
            username = pinResponse?.name,
        )
    }
}

package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(): Flow<UserInfo> = transactionCache
        .pinResponse
        .filterNotNull()
        .map { pinResponse ->
            UserInfo(
                store = pinResponse.store.name,
                username = pinResponse.name,
            )
        }
}

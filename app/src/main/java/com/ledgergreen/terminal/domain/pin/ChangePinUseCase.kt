package com.ledgergreen.terminal.domain.pin

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import timber.log.Timber

class ChangePinUseCase @Inject constructor(
    private val apiService: ApiService,
    private val transactionCache: TransactionCache,
    private val stringResources: StringResources,

    ) {
    suspend operator fun invoke(pin: String): Result<Boolean> = runCatching {
        val userId = transactionCache.pinResponse.value!!.userId.toString()
        apiService.resetPin(
            pin = pin,
            userId = userId,
        )
    }.fold(
        onSuccess = { baseResponse ->
            if (baseResponse.status) {
                Timber.i("PIN changed successfully")
                // this will trigger PIN screen again
                transactionCache.clear()
                Result.success(true)
            } else {
                Timber.w("Unable to reset PIN: ${baseResponse.message}")
                Result.failure(RequestException(baseResponse.message))
            }
        },
        onFailure = {
            Timber.w("Reset PIN failed", it)
            val message = it.displayableErrorMessage(stringResources)
            Result.failure(RequestException(message))
        },
    )
}

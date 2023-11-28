package com.ledgergreen.terminal.domain.password

import com.ledgergreen.terminal.data.AccountRepository
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ResetPasswordUseCase @Inject constructor(
    private val apiService: ApiService,
    private val accountRepository: AccountRepository,
    private val stringResources: StringResources,
    ) {
    suspend operator fun invoke(password: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.resetPassword(password)
        }.fold(
            onSuccess = { baseResponse ->
                if (baseResponse.status && baseResponse.response != null) {
                    // after successful password reset we can continue to use app using
                    // the same token.
                    accountRepository.markPasswordReset()
                    Result.success(baseResponse.response)
                } else {
                    Result.failure(RequestException(baseResponse.message))
                }
            },
            onFailure = {
                Timber.w(it, "Reset password failed")
                val errorMessage = it.displayableErrorMessage(stringResources)
                Result.failure(RequestException(errorMessage))
            },
        )
    }
}

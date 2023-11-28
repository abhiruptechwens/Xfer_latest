package com.ledgergreen.terminal.ui.transactions.domain

import androidx.compose.ui.text.toLowerCase
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

private const val defaultPageSize = 200

class GetRecentTransactionsUseCase @Inject constructor(
    private val apiService: ApiService,
    private val stringResources: StringResources,
) {
    suspend operator fun invoke(
        option:String
    ): Result<List<Transaction>> {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val from = today.atStartOfDayIn(TimeZone.UTC)
        val to = (today + DatePeriod(days = 1)).atStartOfDayIn(TimeZone.UTC)

        Timber.d("Request transactions from [$from] to [$to]")
        return runCatching {
            val baseResponse = apiService.transactionsList(
                from = from,
                to = to,
                page = 1,
                size = defaultPageSize,
                searchQuery = null,
                statuses = null,
                status = option
            )
            if (baseResponse.status && baseResponse.response != null) {
                baseResponse.response.records
            } else {
                throw RequestException(baseResponse.message)
            }
        }.fold(
            onSuccess = {
                Timber.d("Got transactions ${it.size}")
                Result.success(it)
            },
            onFailure = {
                Timber.w(it, "Unable to receive recent transactions")
                val errorMessage = it.displayableErrorMessage(stringResources)
                Result.failure(RequestException(errorMessage))
            },
        )
    }
}

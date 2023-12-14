package com.ledgergreen.terminal.ui.transactions.domain

import androidx.compose.ui.text.toLowerCase
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.Page
import com.ledgergreen.terminal.data.network.model.StringToJsonData
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
import kotlinx.serialization.json.Json
import timber.log.Timber

private const val defaultPageSize = 10

class GetRecentTransactionsUseCase @Inject constructor(
    private val apiService: ApiService,
    private val stringResources: StringResources,
) {
    suspend operator fun invoke(
        option:String,
        searchQuery:String?,
        page:Int
    ): Result<Page> {
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
                page = page,
                size = defaultPageSize,
                searchQuery = searchQuery,
                statuses = null,
                status = option
            )
            if (baseResponse.status && baseResponse.response != null) {
                baseResponse.response
            } else {
                throw RequestException(baseResponse.message)
            }
        }.fold(
            onSuccess = {
//                Timber.d("Got transactions ${it.size}")
                Result.success(it)
            },
            onFailure = {
                val text = it.message

                // Using regular expression
                val regex = Regex("\\{(.+?)\\}")
                val matchResult = regex.find(text!!.asSequence().toString())
                val resultRegex = matchResult?.groups?.get(1)?.value
                println(resultRegex)
                var errorMessage: String =""

                if (text.contains(regex)){
                    val startIndex = text.indexOf('{') + 1
                    val endIndex = text.indexOf('}')
                    val resultSubstring = text.substring(startIndex, endIndex)
                    val resultSubstringInsideCurlyBraces = "{${text.substring(startIndex, endIndex)}}}"

                    val jsonString = resultSubstringInsideCurlyBraces

                    println(jsonString)
                    // Parse the JSON string into a MyData object
                    val myData: StringToJsonData = Json{ ignoreUnknownKeys = true }.decodeFromString(jsonString)
                    errorMessage = myData.message
                }
                else{
                    errorMessage = text
                }

                Result.failure(RequestException(errorMessage))
            },
        )
    }
}

package com.ledgergreen.terminal.domain.pin

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Pin
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.PinResponse
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

class EnterPinUseCase @Inject constructor(
    private val apiService: ApiService,
    private val transactionCache: TransactionCache,
    private val stringResources: StringResources,
    ) {
    suspend operator fun invoke(pin: Pin) = withContext(Dispatchers.IO) {
        runCatching {
            val pinResponse = pinRequest(pin)
            transactionCache.setPinResponse(pinResponse)
        }.fold(
            onSuccess = { Result.success(it) },
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

    private suspend fun pinRequest(pin: Pin): PinResponse {
        val basePinResponse = apiService.pin(pin.code)
        return if (basePinResponse.status) {
            Timber.i("Pin success")
            basePinResponse.response!!
        } else {
            throw RequestException(basePinResponse.message)
        }
    }
}

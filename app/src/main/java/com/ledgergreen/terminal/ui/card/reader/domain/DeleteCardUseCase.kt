package com.ledgergreen.terminal.ui.card.reader.domain

import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.FailedTransaction
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.data.network.model.TransactionGoodsAndServiceRequest
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.sfx.SfxEffect
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

class DeleteCardUseCase @Inject constructor(
    private val apiService: ApiService,
) {

    suspend operator fun invoke(
        cardToken:String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {

            apiService.deleteCard(
               cardToken = cardToken
            )
//            Timber.i("Transaction result ${baseResponse.status}")
//            if (baseResponse.status) {
//                withContext(Dispatchers.IO) {
//                    sfxEffect.playTransactionSuccess()
//                }
//            } else {
//                val isBusinessError =
//                    FailedTransaction.isBusinessError(baseResponse.message)
//
//                throw RequestException(
//                    message = baseResponse.message,
//                    fatal = !isBusinessError,
//                )
//            }
        }.fold(
            onSuccess = {
                Timber.i("Transaction success")
//                analytics.trackCustomAction(Actions.scanTransactionSuccess)
//                trackTips(amount = amount, tips = tips, tipsType = tipsType)

                Result.success(Unit)
            },
            onFailure = {

                AppState1.balanceUpdate = it.message!!
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

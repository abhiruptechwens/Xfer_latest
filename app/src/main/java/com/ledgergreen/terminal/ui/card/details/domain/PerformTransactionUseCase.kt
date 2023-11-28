package com.ledgergreen.terminal.ui.card.details.domain

import android.graphics.Bitmap
import android.util.Base64
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.CardDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.FailedTransaction
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.ComplianceFee
import com.ledgergreen.terminal.data.network.model.SignatureResponse
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.data.network.model.TransactionRequest
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.sfx.SfxEffect
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.round2Decimals
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

val canadaZipRegex = Regex("^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d\$")
val usZipRegex = Regex("^\\d{5}(?:[-\\s]\\d{4})?$")
//val anyZipRegex = Regex("^[a-zA-Z0-9]{4} ?[a-zA-Z0-9]{4}\$")
val anyZipRegex = Regex("^\\w{5,}[\\s\\w-]*\$")

class PerformTransactionUseCase @Inject constructor(
    private val apiService: ApiService,
    private val transactionCache: TransactionCache,
    private val sfxEffect: SfxEffect,
    private val stringResources: StringResources,
    private val analytics: Analytics,
) {
    suspend operator fun invoke(
        card: CardDetails,
        amount: Money,
        tips: Money,
        tipsType: String?,
        cardFee: Double,
        cardType: String,
        amountPlusFee: Double,
        cardToken: String?,
        saveCard: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val signature = transactionCache.signature.first()
                ?: error(stringResources.getString(R.string.error_signature_is_not_set))

            val customerId = transactionCache.customer.value!!.customerId
            val userId = transactionCache.pinResponse.first()!!.userId

            val signatureResponse = saveSignature(
                signature = signature,
                customerId = customerId,
                userId = userId,
            )
            Timber.d("Signature saved")

            val originalZipCode = card.billingAddressZip
            // if this is a US zip code, we need only the first 5 digits, and not the full ZIP+4
            val usZipLength = 5
            val zipCode = if (originalZipCode.matches(usZipRegex)
                && originalZipCode.length > usZipLength
            ) {
                originalZipCode.take(usZipLength)
            } else {
                originalZipCode
            }

            val baseResponse = apiService.transaction(
                TransactionRequest(
                    activityId = signatureResponse.activityId,
                    amount = amount.toDouble(),
                    tip = tips.toDouble(),
                    cardNumber = if(cardToken.equals("0")) card.number else "",
                    cardHolderName = if(cardToken.equals("0")) card.holderName else "",
                    month = if(cardToken.equals("0")) card.month else "",
                    year = if(cardToken.equals("0")) card.year else "",
                    cvv = if(cardToken.equals("0")) card.cvv else "",
                    postalCode = zipCode,
                    customerId = customerId,
                    cardFee = cardFee,
                    cardType = cardType,
                    amountPlusFee = amountPlusFee,
                    cardToken = if (cardToken.equals("0")) "" else cardToken,
                    saveCard = saveCard
                ),
            )
            Timber.i("Transaction result ${baseResponse.status}")
            if (baseResponse.status) {
                transactionCache.setTransactionResult(baseResponse.response!!)
                withContext(Dispatchers.IO) {
//                    sfxEffect.playTransactionSuccess()
                }
            } else {
                val isBusinessError =
                    FailedTransaction.isBusinessError(baseResponse.message)

                throw RequestException(
                    message = baseResponse.message,
                    fatal = !isBusinessError,
                )
            }
        }.fold(
            onSuccess = {
                Timber.i("Transaction success")
                analytics.trackCustomAction(Actions.scanTransactionSuccess)
                trackTips(amount = amount, tips = tips, tipsType = tipsType)

                Result.success(Unit)
            },
            onFailure = {
                val logMessage = "Unable to perform transaction"

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

    private suspend fun saveSignature(
        signature: Signature,
        customerId: Long,
        userId: Long,
    ): SignatureResponse =
        withContext(Dispatchers.IO) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            signature.bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val signatureBase64WithPrefix = "data:image/png;base64,$base64String"

            val signatureBaseResponse = apiService.signature(
                customerId = customerId,
                userId = userId,
                signatureBase64 = signatureBase64WithPrefix,
            )
            if (signatureBaseResponse.status) {
                signatureBaseResponse.response!!
            } else {
                throw RequestException(stringResources.getString(R.string.error_unable_to_send_signature))
            }
        }

    private fun trackTips(
        amount: Money,
        tips: Money,
        tipsType: String?,
    ) {
        val tipsPercentage = (tips.toDouble() / amount.toDouble() * 100)
            .round2Decimals()

        analytics.trackCustomAction(
            name = Actions.tipsSelected,
            attributes = mapOf(
                "tips_type" to tipsType,
                "tips_amount" to tips.toDouble(),
                "tips_percentage" to tipsPercentage,
            ),
        )

    }
}

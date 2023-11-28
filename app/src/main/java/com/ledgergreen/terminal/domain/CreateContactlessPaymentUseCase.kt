package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.domain.exception.ValidationException
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.qr.QrCode
import com.ledgergreen.terminal.qr.QrGenerator
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.serialization.json.Json
import timber.log.Timber

data class ContactlessPaymentInput(
    val phoneNumber: PhoneNumber,
    val customerName: String,
    val amount: Money,
    val sendSms: Boolean,
)

data class ContactlessPayment(
    val smsSent: Boolean,
    val qrCode: QrCode,
)

class CreateContactlessPaymentUseCase @Inject constructor(
    private val apiService: ApiService,
    private val qrGenerator: QrGenerator,
    private val stringResources: StringResources,
    private val transactionCache: TransactionCache,
    private val analytics: Analytics,
) {
    suspend operator fun invoke(
        input: ContactlessPaymentInput,
    ): Result<ContactlessPayment> = runCatching {
        when {
            input.phoneNumber.phone.isBlank() -> R.string.validation_phone_blank

            input.phoneNumber.isUSPhone() &&
                input.phoneNumber.phone.length != PhoneNumber.USExactPhoneLength ->
                R.string.validation_phone_10_symbols

            !input.phoneNumber.isUSPhone() &&
                input.phoneNumber.phone.length < PhoneNumber.minPhoneLength ->
                R.string.validation_phone_at_least_6_symbols

            input.customerName.isBlank() -> R.string.validation_customer_name_blank
            input.amount.toDouble() < minAmount -> R.string.validation_amount_greater_than_1_0
            else -> null
        }?.let { throw ValidationException(stringResources.getString(it)) }

        val userId = transactionCache.pinResponse.value?.userId
            ?: error("Unable to create contactless payment. pinResponse is not set")
        val storeId = transactionCache.pinResponse.value?.storeId
            ?: error("Unable to create contactless payment. pinResponse is not set")

        val baseResponse = apiService.contactless(
            userId = userId,
            storeId = storeId,
            customerName = input.customerName,
            phoneCode = input.phoneNumber.phoneCode.toString(),
            phoneNumber = input.phoneNumber.phone,
            amount = input.amount,
            sendSms = input.sendSms,
        )

        if (baseResponse.status && baseResponse.response != null) {
            val url = baseResponse.response.url
            val qrCode = qrGenerator.generateForUrl(url)
            ContactlessPayment(
                smsSent = baseResponse.response.smsSent,
                qrCode = qrCode,
            )
        } else {
            throw RequestException(baseResponse.message)
        }
    }.fold(
        onSuccess = {
            analytics.trackCustomAction(
                name = Actions.phoneNumberAttached,
                attributes = mapOf(
                    "phone_country" to input.phoneNumber.countryCode.countryName,
                ),
            )
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

    companion object {
        private const val minAmount = 1.0
    }
}

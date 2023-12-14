package com.ledgergreen.terminal.domain.scan

import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.CustomerIdType
import com.ledgergreen.terminal.data.network.model.CustomerRegistrationState
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.KycRequest
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.data.network.toApiLocalDateString
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.scanner.Document
import javax.inject.Inject
import kotlinx.serialization.json.Json
import timber.log.Timber

sealed class FindCustomerByDocumentResult {
    data class CustomerFound(
        val customerResponse: CustomerResponse?,
    ) : FindCustomerByDocumentResult()

    data object CustomerNotFound : FindCustomerByDocumentResult()
}

class FindCustomerByDocumentUseCase @Inject constructor(
    private val apiService: ApiService,
    private val analytics: Analytics,
) {
    suspend operator fun invoke(
        document: Document,
    ): Result<FindCustomerByDocumentResult> = runCatching {
        val kycRequest = document.toKycRequest().also {
            analytics.trackCustomAction(
                Actions.documentScan,
                mapOf("id_type" to it.idType),
            )
        }
        apiService.kyc(kycRequest)
    }.fold(
        onSuccess = { baseResponse ->
            if (baseResponse.status && baseResponse.response != null) {
                val kycResponse = baseResponse.response

                val result = when (kycResponse.registrationState) {
                    CustomerRegistrationState.Exist -> {
                        Timber.i("Customer is registered")
                        FindCustomerByDocumentResult.CustomerFound(kycResponse.customer!!)
                    }

                    CustomerRegistrationState.New -> {
                        Timber.i("Customer is not registered. Registration required")
                        FindCustomerByDocumentResult.CustomerNotFound
                    }
                }
                Result.success(result)
            } else {
                Result.failure(RequestException(baseResponse.message))
            }
        },
        onFailure = {
            val text = it.message

            var errorMessage: String = ""
            if(it.message!!.contains("You are not allowed to use this system.")){
                errorMessage = "Account Restricted!\nPlease contact Xfer support at 833-206-5989."
            }
            else{
//                val regex = Regex("\\{(.+?)\\}")
//                val matchResult = regex.find(text ?: "")
//                val resultRegex = matchResult?.groups?.get(1)?.value
//                println(resultRegex)
//
//
//
//                if (resultRegex != null) {
//                    // If a match is found, extract the substring inside curly braces
//                    val jsonString = "{$resultRegex}"
//
//                    println(jsonString)
//                    // Parse the JSON string into a MyData object
//                    val myData: StringToJsonData = Json { ignoreUnknownKeys = true }.decodeFromString(jsonString)
//                    errorMessage = myData.message
//                } else {
//                    errorMessage = text ?: ""
//                }


                val regex = Regex("\\{(.+?)\\}")
                val matchResult = regex.find(text!!.asSequence().toString())
                val resultRegex = matchResult?.groups?.get(1)?.value
                println(resultRegex)

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
            }
// Using regular expression
            Result.failure(RequestException(errorMessage))
        },
    )

    private fun Document.toKycRequest(): KycRequest = when (this) {
        is Document.DriverLicense -> KycRequest(
            idNumber = documentNumber,
            idType = CustomerIdType.DriverLicense,
            address1 = address1,
            birthdate = birthDate?.toApiLocalDateString(),
            city = city,
            state = state,
            country = country,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            expirationDate = expiryDate.toApiLocalDateString(),
            issueDate = issueDate.toApiLocalDateString(),
            gender = sex,
            postalCode = postalCode,
        )

        is Document.MrzDocument -> KycRequest(
            idNumber = documentNumber,
            idType = CustomerIdType.Document,
            address1 = null,
            birthdate = birthDate.toApiLocalDateString(),
            city = null,
            state = null,
            country = country,
            firstName = firstName,
            lastName = lastName,
            middleName = null,
            expirationDate = expiryDate.toApiLocalDateString(),
            issueDate = null,
            gender = sex,
            postalCode = null,
        )
    }
}

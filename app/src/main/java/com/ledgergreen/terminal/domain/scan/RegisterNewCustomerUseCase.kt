package com.ledgergreen.terminal.domain.scan

import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.RegisterCustomerRequest
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.data.network.toApiLocalDateString
import com.ledgergreen.terminal.domain.exception.validationError
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.serialization.json.Json
import timber.log.Timber

class RegisterNewCustomerUseCase @Inject constructor(
    private val apiService: ApiService,
    private val stringResources: StringResources,
    private val proceedPurchaseWithCustomerUseCase: ProceedPurchaseWithCustomerUseCase,
    private val analytics: Analytics,
) {
    suspend operator fun invoke(
        registrationForm: CustomerRegistrationForm,
    ): Result<CustomerResponse> {
        val registerRequest = runCatching {
            createRegisterRequest(registrationForm)
        }.fold(
            onSuccess = { it },
            onFailure = {
                Timber.w(it, "Unable to create RegisterCustomerRequest. Validation failed")
                return Result.failure(it)
            },
        )

        return runCatching {
            val baseRegisterResponse = apiService.registerCustomer(registerRequest)
            if (baseRegisterResponse.status && baseRegisterResponse.response != null) {
                baseRegisterResponse.response.also {
                    proceedPurchaseWithCustomerUseCase(it)
                }
            } else {
                throw RequestException(baseRegisterResponse.message)
            }
        }.fold(
            onSuccess = {
                Timber.i("Customer registered successfully")
                analytics.trackCustomAction(
                    name = Actions.phoneNumberAttached,
                    attributes = mapOf(
                        "phone_country" to registrationForm.phoneNumber!!.countryCode.countryName,
                    ),
                )
                Result.success(it)
            },
            onFailure = {


                Timber.w(it, "Failed to register new customer")

                val text = it.message
                var errorMessage: String = ""
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

//                val errorMessage = it.displayableErrorMessage(stringResources)
                Result.failure(RequestException(errorMessage))
            },
        )
    }

    private fun createRegisterRequest(form: CustomerRegistrationForm): RegisterCustomerRequest =
        RegisterCustomerRequest(
            idNumber = form.idNumber,
            idType = form.idType,
            address1 = form.address1 ?: validationError("Address is not set"),
            birthdate = form.birthdate?.toApiLocalDateString(),
            city = form.city ?: validationError("City is not set"),
            state = form.state ?: validationError("State is not set"),
            country = form.country?: validationError("Country is not set"),
            countryCode = form.phoneNumber?.countryCode?.phoneCode
                ?: validationError("Phone is not set"),
            phone = form.phoneNumber.phone,
            firstName = form.firstName?: validationError("First Name is not set"),
            lastName = form.lastName?: validationError("Last Name is not set"),
            middleName = form.middleName?:"",
            expirationDate = form.expirationDate.toApiLocalDateString(),
            issueDate = form.issueDate?.toApiLocalDateString()
                ?: validationError("Issue date is not set"),
            gender = form.gender,
            postalCode = form.postalCode ?: validationError("Postal code is not set"),
        )
}

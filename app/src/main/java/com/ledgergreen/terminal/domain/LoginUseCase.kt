package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.data.AccountRepository
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.StringToJsonData
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import kotlinx.serialization.json.Json
import timber.log.Timber

class LoginUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val stringResources: StringResources,
) {
    suspend operator fun invoke(login: String, password: String): Result<Boolean> =
        runCatching {
            accountRepository.login(login, password)
        }.fold(
            onSuccess = {
                Result.success(true)
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

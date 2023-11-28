package com.ledgergreen.terminal.domain.customer

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.ApiService
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.data.network.model.CustResponse
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.WalletDetails
import com.ledgergreen.terminal.domain.scan.FindCustomerByDocumentResult
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import javax.inject.Inject
import timber.log.Timber



sealed class FindCustomerByCustomerIdResult {
    data class CustomerFound(
        val customerResponse: CustResponse
    ) : FindCustomerByCustomerIdResult()
}

class FindCustomerByCustomerIdUseCase @Inject constructor(
    private val apiService: ApiService,
    private val transactionCache: TransactionCache,
    private val stringResources: StringResources,

    ){

    suspend operator fun invoke(custId: String) :Result<FindCustomerByCustomerIdResult> =
        runCatching {
            val custResponse = cusRequest(custId)
            transactionCache.setCustomerValue(custResponse)

            apiService.getCustomer(custId)
        }.fold(
            onSuccess = {
//                Result.success(it)
                    baseResponse ->
                ((if (baseResponse.status && baseResponse.response != null) {
                    val custResponse = baseResponse.response
                    val result = FindCustomerByCustomerIdResult.CustomerFound(custResponse)
                    Result.success(result)
                } else {
                    Result.failure(RequestException(baseResponse.message))
                }) as Result<FindCustomerByCustomerIdResult>)
            },
            onFailure = {
                Timber.w(it, "Unable to get Customer")
                val errorMessage = it.displayableErrorMessage(stringResources)
                Result.failure(RequestException(errorMessage))
            },
        )

    private suspend fun cusRequest(custID: String): CustResponse {
        val baseCustResponse = apiService.getCustomer(custID)
        return if (baseCustResponse.status) {
            Timber.i("customer found")
            baseCustResponse.response!!
        } else {
            throw RequestException(baseCustResponse.message)
        }
    }

}

package com.ledgergreen.terminal.data.network

import com.datadog.android.DatadogInterceptor
import com.datadog.android.rum.RumInterceptor
import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.network.model.BaseResponse
import com.ledgergreen.terminal.data.network.model.ComplianceFee
import com.ledgergreen.terminal.data.network.model.ComplianceFeeRequest
import com.ledgergreen.terminal.data.network.model.ContactlessListRequest
import com.ledgergreen.terminal.data.network.model.ContactlessListSearch
import com.ledgergreen.terminal.data.network.model.ContactlessRequest
import com.ledgergreen.terminal.data.network.model.ContactlessResponse
import com.ledgergreen.terminal.data.network.model.CustRequest
import com.ledgergreen.terminal.data.network.model.CustResponse
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.GetCustomer
import com.ledgergreen.terminal.data.network.model.KycRequest
import com.ledgergreen.terminal.data.network.model.KycResponse
import com.ledgergreen.terminal.data.network.model.LoginRequest
import com.ledgergreen.terminal.data.network.model.LoginResponse
import com.ledgergreen.terminal.data.network.model.Page
import com.ledgergreen.terminal.data.network.model.PinRequest
import com.ledgergreen.terminal.data.network.model.PinResponse
import com.ledgergreen.terminal.data.network.model.RegisterCustomerRequest
import com.ledgergreen.terminal.data.network.model.ResetPasswordRequest
import com.ledgergreen.terminal.data.network.model.ResetPinRequest
import com.ledgergreen.terminal.data.network.model.SignatureRequest
import com.ledgergreen.terminal.data.network.model.SignatureResponse
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.data.network.model.TransactionGoodsAndServiceRequest
import com.ledgergreen.terminal.data.network.model.TransactionGoodsAndServiceResponse
import com.ledgergreen.terminal.data.network.model.TransactionRequest
import com.ledgergreen.terminal.data.network.model.TransactionResponse
import com.ledgergreen.terminal.data.network.model.TransactionStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

interface TokenStore {
    fun getToken(): String?
    fun invalidate()
}

/** https://ledgergreeninc.atlassian.net/wiki/spaces/LG/pages/46202895/Android+API+Documentaion */
@Singleton
class ApiService @Inject constructor(
    baseUrl: String,
    xAccessKey: String,
    private val tokenStore: TokenStore,
) {

    private val httpClient = HttpClient(OkHttp) {
        expectSuccess = true
        engine {
            addInterceptor(DatadogInterceptor())
            addInterceptor(RumInterceptor())
            addNetworkInterceptor(TokenInvalidatorInterceptor(tokenStore))
        }
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.d(message)
                }
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
            sanitizeHeader { header -> header == accessKeyHeader }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 25_000 // Set the desired timeout value in milliseconds
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 60_000
        }

        defaultRequest {
//            url("$baseUrl/v1/")
            url("$baseUrl/api/v1/")
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(accessKeyHeader, xAccessKey)
            if (!this.url.encodedPath.contains("loginforterminal")) {
                tokenStore.getToken()?.let {
                    header("Authorization", "Bearer $it")
                }
            }
        }
    }

    suspend fun login(username: String, password: String): BaseResponse<LoginResponse> = httpClient
        .post(urlString = "loginforterminal") {
//        .post(urlString = "api/auth/login") {
            setBody(LoginRequest(username, password))
        }.body()

    suspend fun pin(pinCode: String): BaseResponse<PinResponse> =
        httpClient.post(urlString = "verifypinforterminal") {
//        httpClient.post(urlString = "app/auth/verify/pin") {
            setBody(PinRequest(pinCode))
        }.body()

    suspend fun contactless(
        userId: Long,
        storeId: String,
        customerName: String,
        phoneCode: String,
        phoneNumber: String,
        amount: Money,
        sendSms: Boolean,
    ): BaseResponse<ContactlessResponse> = httpClient.post(urlString = "auth/contactless") {
        setBody(
            ContactlessRequest(
                userId = userId,
                storeId = storeId,
                customerName = customerName,
                phoneCode = phoneCode,
                phoneNumber = phoneNumber,
                amount = amount.toDouble(),
                sendSms = sendSms,
            ),
        )
    }.body()

    suspend fun kyc(kycRequest: KycRequest): BaseResponse<KycResponse> =
        httpClient.post(urlString = "scan") {
//        httpClient.post(urlString = "app/auth/get/kyc") {
            setBody(kycRequest)
        }.body()


    suspend fun getCustomer(customerId: String): BaseResponse<CustResponse> =
        httpClient.post(urlString = "get-customer") {
//        httpClient.post(urlString = "app/auth/get/kyc") {
            setBody(CustRequest(customerId))
        }.body()

    suspend fun deleteCard(cardToken: String) =
        httpClient.post(urlString = "delete-card/$cardToken") {
//        httpClient.post(urlString = "app/auth/get/kyc") {
        }

    suspend fun registerCustomer(
        registerRequest: RegisterCustomerRequest,
    ): BaseResponse<CustomerResponse> =
        httpClient.post(urlString = "app/auth/kyc") {
            setBody(registerRequest)
        }.body()

    suspend fun signature(
        customerId: Long,
        userId: Long,
        signatureBase64: String,
    ): BaseResponse<SignatureResponse> =
        httpClient.post(urlString = "app/auth/signature") {
            setBody(
                SignatureRequest(
                    customerId = customerId,
                    userId = userId,
                    signature = signatureBase64,
                ),
            )
        }.body()

    // TODO (Ivan): use base response, breaking change for this API call
    suspend fun complianceFee(cardNumber: String, amount: Double): ComplianceFee =
        httpClient.post(urlString = "app/auth/compliance/fee") {
            setBody(ComplianceFeeRequest(cardNumber, amount))
        }.body()

    suspend fun transaction(transactionRequest: TransactionRequest): BaseResponse<TransactionResponse> =
        httpClient.post(urlString = "app/auth/transaction") {
            setBody(transactionRequest)
        }.body()

    suspend fun transactionGoodsAndService(transactionRequest: TransactionGoodsAndServiceRequest, customerId: String): BaseResponse<TransactionGoodsAndServiceResponse> =
        httpClient.post(urlString = "goods-and-services/$customerId") {
            setBody(transactionRequest)
        }.body()


    suspend fun resetPassword(password: String): BaseResponse<String> =
        httpClient.post(urlString = "app/auth/reset/password") {
            setBody(
                ResetPasswordRequest(password),
            )
        }.body()

    suspend fun resetPin(pin: String, userId: String): BaseResponse<String> =
        httpClient.post(urlString = "app/auth/reset/pin") {
            setBody(
                ResetPinRequest(pin = pin, userId = userId),
            )
        }.body()

    suspend fun transactionsList(
        from: Instant,
        to: Instant,
        page: Int,
        size: Int,
        searchQuery: String?,
        statuses: List<TransactionStatus>?,
        status: String
    ): BaseResponse<Page<Transaction>> = httpClient.post(urlString = "getContactList") {
        setBody(
            ContactlessListRequest(
//                from = from,
//                to = to,
                page = page,
                size = size,
                s = if (searchQuery != null || statuses != null) ContactlessListSearch(
                    query = searchQuery,
                    status = statuses,
                ) else null,
                status = status,
            ),
        )
    }.body()

    companion object {
        const val accessKeyHeader = "x-access-key"
    }
}

class TokenInvalidatorInterceptor(private val invalidator: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 403) {
            invalidator.invalidate()
        }
        return response
    }
}

fun LocalDate.toApiLocalDateString() = toString().replace("-", "/")

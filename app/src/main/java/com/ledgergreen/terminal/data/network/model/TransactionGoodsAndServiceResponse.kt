package com.ledgergreen.terminal.data.network.model

import com.ledgergreen.terminal.data.model.Money
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TransactionGoodsAndServiceRequest(
    val amount: Double,
    val tip: Double,
    val storeId: String,
    @SerialName("user_id")
    val userId: String,
)
data class TransactionGoodsAndServiceStart(
    val message: String,
    val response: Response,
    val status: Boolean,
    val statusCode: Int
)
@Serializable
data class TransactionGoodsAndServiceResponse(
    val amount: Double,
    val balance: Money,
    val created_at: String,
    val customer_account_number: String,
    val name: String,
    val order_id: String,
    val phone: String,
    val url: String
)

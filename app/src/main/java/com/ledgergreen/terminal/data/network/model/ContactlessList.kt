package com.ledgergreen.terminal.data.network.model

import com.ledgergreen.terminal.data.model.Money
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionStatus {
    @SerialName("PAID")
    Paid,

    @SerialName("EXPIRED")
    Expired,

    @SerialName("PENDING")
    Pending,
}

@Serializable
data class ContactlessListSearch(
    val query: String?,
    val status: List<TransactionStatus>?,
)

@Serializable
data class ContactlessListRequest(
//    val from: Instant,
//    val to: Instant,
    val page: Int,
    val size: Int,
    val s: ContactlessListSearch?,
    val status: String,
)

@Serializable
data class Transaction(
    val id: String,
    val customerName: String,
    val phone: String,
    val amount: String,
    val status: TransactionStatus,
    val associate: String,
    val associateId: Int,
    @SerialName("last4")
    val cardNumber: String?,
)

@Serializable
data class Associate(
    val id: Long,
    val name: String,
)

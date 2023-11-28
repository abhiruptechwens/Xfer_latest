package com.ledgergreen.terminal.data.network.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    @SerialName("activities")
    val activityId: Long,
    val amount: Double,
    val tip: Double,
    @SerialName("CardNumber")
    val cardNumber: String?,
    @SerialName("card_holder_name")
    val cardHolderName: String?,
    @SerialName("CardExpiryMonth")
    val month: String?,
    @SerialName("CardExpiryYear")
    val year: String?,
    @SerialName("CVV2")
    val cvv: String?,
    @SerialName("Zip")
    val postalCode: String?,
    @SerialName("CustomerId")
    val customerId: Long,
    @SerialName("card_fee")
    val cardFee: Double,
    @SerialName("card_type")
    val cardType: String?, // DEBIT / CREDIT
    @SerialName("amount_plus_fee")
    val amountPlusFee: Double,
    @SerialName("card_token")
    val cardToken: String?,
    val saveCard : Boolean


)

@Serializable
data class TransactionResponse(
    val location: String = "", // must not be null but server may not return this field
//    @SerialName("terminal_id")
//    val terminalId: String,
    @SerialName("order_id")
    val orderId: String,
    val receiptId: String,
    val amount: Double,
//    @SerialName("order_amount")
    val orderAmount: Double,
//    val tips: Double,
//    val fees: Double,
//    @SerialName("transaction_id")
    val transactionId: String,
//    @SerialName("card_number")
//    val cardNumber: String,
//    @SerialName("card_type")
//    val cardType: String,
    @SerialName("name")
    val customerName: String,
    @SerialName("phone")
    val customerPhone: String? = null,
    @SerialName("customer_account_number")
    val customerAccountNumber: String,
    @SerialName("url")
    val signature: String,
    val paymentFor: String,
    @SerialName("created_at")
    val date: Instant,
    val customerExtId: String,
//    val lastTerminalId: String,
)

package com.ledgergreen.terminal.ui.receipt

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Immutable
data class ReceiptState(
    val orderId: String,
    val orderAmount: String,
    val totalAmount: String,
    val amount: String,
//    val tipAmount: String,
//    val complianceFee: String,
    val orderDate: Instant,
//    val cardNumber: String,
//    val cardType: String,
    val customerName: String,
    val accountNumber: String,
    val location: String,
//    val terminal: String,
    val customerPhoneNumber: String,
    val signatureUrl: String,
    val receiptId: String,
    val customerExtId : String,
    var onNext : Boolean,
    var isLoading : Boolean,
) {
    companion object {
        val EMPTY = ReceiptState(
            orderId = "",
            totalAmount = "",
            amount = "",
//            tipAmount = "",
//            complianceFee = "",
            orderDate = Clock.System.now(),
//            cardNumber = "",
//            cardType = "",
            customerName = "",
            accountNumber = "",
            location = "",
//            terminal = "",
            customerPhoneNumber = "",
            signatureUrl = "",
            receiptId = "",
            customerExtId = "",
            onNext = false,
            orderAmount = "",
            isLoading = false
        )
    }
}

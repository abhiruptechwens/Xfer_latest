package com.ledgergreen.terminal.data

import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.PageState
import com.ledgergreen.terminal.data.model.AmountDetails
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.data.network.model.CustResponse
import com.ledgergreen.terminal.data.network.model.GetCustomer
import com.ledgergreen.terminal.data.network.model.PinResponse
import com.ledgergreen.terminal.data.network.model.TransactionGoodsAndServiceResponse
import com.ledgergreen.terminal.data.network.model.TransactionResponse
import com.ledgergreen.terminal.pos.card.CardDetails
import com.ledgergreen.terminal.scanner.Document
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

data class CustomerTransactionData(
    val customerId: Long,
    val fullName: String,
)

@Suppress("EmptyDefaultConstructor")
@Singleton
class TransactionCache @Inject constructor() {

    private val _amountDetails = MutableStateFlow<AmountDetails?>(null)

    private val _document = MutableStateFlow<Document?>(null)
    private val _customer = MutableStateFlow<CustomerTransactionData?>(null)
    private val _customerValue = MutableStateFlow<CustResponse?>(null)
    private val _signature = MutableStateFlow<Signature?>(null)
    private val _transactionResponse = MutableStateFlow<TransactionResponse?>(null)
    private val _transactionGoodsAndServiceResponse = MutableStateFlow<TransactionGoodsAndServiceResponse?>(null)
    private val _pinResponse = MutableStateFlow<PinResponse?>(null)
    private val _cardReaderResult = MutableStateFlow<CardDetails?>(null)
    private var _fromPage = ""

    val document: StateFlow<Document?> = _document
    val amountDetails: StateFlow<AmountDetails?> = _amountDetails
    val customer: StateFlow<CustomerTransactionData?> = _customer
    val signature: StateFlow<Signature?> = _signature
    val transactionResponse: StateFlow<TransactionResponse?> = _transactionResponse
    val pinResponse: StateFlow<PinResponse?> = _pinResponse
    val cardReaderResult: StateFlow<CardDetails?> = _cardReaderResult
    val custWalletResponse: StateFlow<CustResponse?> = _customerValue
    val transactionGoodsAndServiceResponse: StateFlow<TransactionGoodsAndServiceResponse?> = _transactionGoodsAndServiceResponse
    val fromPage: String = _fromPage.toString()

    fun setPinResponse(pinResponse: PinResponse) {
        _pinResponse.value = pinResponse
    }

    fun setDocument(document: Document?) {
        _document.value = document
    }

    fun setAmountDetails(amountDetails: AmountDetails) {
        _amountDetails.value = amountDetails
    }

    fun setCustomer(customer: CustomerTransactionData) {
        _customer.value = customer
    }

    fun setCustomerValue(customerValue: CustResponse) {
        _customerValue.value = customerValue
    }

    fun setSignature(signature: Signature) {
        _signature.value = signature
    }

    fun clearTransaction() {
        Timber.i("Clear transaction cache")
        _document.value = null
        _amountDetails.value = null
        _customer.value = null
        _signature.value = null
        _transactionResponse.value = null
        _cardReaderResult.value = null
    }

    fun clearTransactionResponse() {
        Timber.i("Clear transaction cache")
        _transactionResponse.value = null
        _transactionGoodsAndServiceResponse.value = null
        AppState1.tips = 0.0
        AppState1.inputAmount = ""
        AppState1.cardDetails = null
        AppState1.savedCardDetails = null
        PageState.fromPage = ""
        AppState1.amountPlusFee = null
        AppState1.cardFee = 0.0
        AppState1.cardType = ""
        AppState1.balanceUpdate = ""
        AppState1.balanceAmount = null
    }

    fun clear() {
        Timber.i("Clear transaction and pin data")
        clearTransaction()
        _pinResponse.value = null
    }

    fun setTransactionResult(transactionResponse: TransactionResponse) {
        _transactionResponse.value = transactionResponse
    }

    fun setTransactionGoodsAndServiceResult(transactionResponse: TransactionGoodsAndServiceResponse) {
        _transactionGoodsAndServiceResponse.value = transactionResponse
    }

    fun setCardData(cardReaderResult: CardDetails?) {
        _cardReaderResult.value = cardReaderResult
    }

    fun setFromPage(from:String){
        _fromPage = fromPage
    }
}

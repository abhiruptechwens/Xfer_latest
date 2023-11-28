package com.ledgergreen.terminal.ui.card.details

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.model.CardDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.ui.agreement.domain.GetAgreementUseCase
import com.ledgergreen.terminal.ui.amount.domain.SaveAmountUseCase
import com.ledgergreen.terminal.ui.card.details.domain.GetComplianceFeeUseCase
import com.ledgergreen.terminal.ui.card.details.domain.GetTransactionInputUseCase
import com.ledgergreen.terminal.ui.card.details.domain.PerformGoodsAndServiceTransactionUseCase
import com.ledgergreen.terminal.ui.card.details.domain.PerformTransactionUseCase
import com.ledgergreen.terminal.ui.card.details.domain.ValidateCardNumberUseCase
import com.ledgergreen.terminal.ui.card.details.domain.anyZipRegex
import com.ledgergreen.terminal.ui.card.details.domain.canadaZipRegex
import com.ledgergreen.terminal.ui.card.details.domain.usZipRegex
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.failure.isNetworkException
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val getConvenienceFee: GetComplianceFeeUseCase,
    private val performTransaction: PerformTransactionUseCase,
    private val getCardDetailsInput: GetTransactionInputUseCase,
    private val validateCardNumberUseCase: ValidateCardNumberUseCase,
    private val stringResources: StringResources,
    private val transactionCache: TransactionCache,
    private val setAmount: SaveAmountUseCase,
    private val analytics: Analytics,
    private val getAgreement: GetAgreementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        getCardDetailsInput().let { transactionInput ->
            CardDetailsState(
                cardNumber = transactionInput.cardNumber ?: "",
                expiryDate = transactionInput.expiryDate ?: "",
                cardHolderName = transactionInput.cardHolderName,
                cvv = "",
                zipCode = "",
                amountDetails = transactionInput.amountDetails,
                convenienceCharge = null,
                total = null,
                navigateNext = false,
                cardType = CardType.UNKNOWN,
                cardConstraints = defaultCardConstraints,
                onErrorShown = ::onErrorShown,
                cardFee = 0.0,
                amountPlusFee = null,
                agreementText = "",
                errorMsg = null,
                saveCard = false,
            )
        },
    )


    val state: StateFlow<CardDetailsState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val amount = state.value.amountDetails!!
            val orderAndTips= AppState1.balanceAmount!!+amount.tips

            if(AppState1.savedCardDetails!=null){
                state.map { getConvenienceFee(AppState1.savedCardDetails!!.cardNumber.substring(0,
                    minOf(AppState1.savedCardDetails!!.cardNumber.length,6)
                ), orderAndTips) }
                    .collect { convenienceFee ->
                        val state = state.value
                        val total =
                            state.amountDetails!!.order + state.amountDetails.tips + convenienceFee

                        _state.value = state.copy(
                            convenienceCharge = convenienceFee,
                            total = total,
                            amountPlusFee = state.amountDetails.order!!+convenienceFee
                        )
                    }
            }
            else{
                state.map { it.cardNumber }
                    .filter { it.length >= 6 }
                    .map { it.take(6) }
                    .distinctUntilChanged()
                    .map { cardFirst6Digits ->
                        if (AppState1.savedCardDetails!=null)
                            getConvenienceFee(AppState1.savedCardDetails!!.cardNumber.substring(0,
                                minOf(AppState1.savedCardDetails!!.cardNumber.length,6)
                            ), orderAndTips)
                        else
                            getConvenienceFee(cardFirst6Digits, orderAndTips)
                    }
                    .retryWhen { cause, attempt ->
                        val retry = attempt <= 5 && cause.isNetworkException()
                        Timber.w(cause, "Failed to get convenience fee. Retry? $retry")
                        delay(500L)
                        retry
                    }
                    .catch {
                        Timber.w(it, "Unable to get compliance fee")

                        val exceptionError = it.displayableErrorMessage(stringResources)

                        val fullError =
                            stringResources.getString(
                                R.string.error_compliance_fee_with_message,
                                exceptionError,
                            )

                        _state.value = state.value.copy(transactionError = fullError)
                    }
                    .collect { convenienceFee ->
                        val state = state.value
                        val total =
                            state.amountDetails!!.order + state.amountDetails.tips + convenienceFee

                        _state.value = state.copy(
                            convenienceCharge = convenienceFee,
                            total = total,
                            amountPlusFee = AppState1.balanceAmount!!+convenienceFee
                        )
                    }
            }

            state.map { it.cardNumber }
                .filter { it.length >= 6 }
                .map { it.take(6) }
                .distinctUntilChanged()
                .map { cardFirst6Digits ->
                    if (AppState1.savedCardDetails!=null)
                        getConvenienceFee(AppState1.savedCardDetails!!.cardNumber.substring(0,
                            minOf(AppState1.savedCardDetails!!.cardNumber.length,6)
                        ), orderAndTips)
                    else
                        getConvenienceFee(cardFirst6Digits, orderAndTips)
                }
                .retryWhen { cause, attempt ->
                    val retry = attempt <= 5 && cause.isNetworkException()
                    Timber.w(cause, "Failed to get convenience fee. Retry? $retry")
                    delay(500L)
                    retry
                }
                .catch {
                    Timber.w(it, "Unable to get compliance fee")

                    val exceptionError = it.displayableErrorMessage(stringResources)

                    val fullError =
                        stringResources.getString(
                            R.string.error_compliance_fee_with_message,
                            exceptionError,
                        )

                    _state.value = state.value.copy(transactionError = fullError)
                }
                .collect { convenienceFee ->
                    val state = state.value
                    val total =
                        state.amountDetails!!.order + state.amountDetails.tips + convenienceFee

                    _state.value = state.copy(
                        convenienceCharge = convenienceFee,
                        total = total,
                        amountPlusFee = AppState1.balanceAmount!!+convenienceFee
                    )
                }
        }

        viewModelScope.launch(Dispatchers.Default) {
            state.map { it.cardNumber }
                .distinctUntilChanged()
                .collect { cardNumber ->
                    val cardType = getCardTypeFromNumber(cardNumber)
                    val shouldDropCvv = state.value.cardType != cardType
                    val cardConstraints = if (cardType == CardType.AMERICAN_EXPRESS) {
                        amexCardConstraints
                    } else {
                        defaultCardConstraints
                    }
                    val isValidCardNumber =
                        validateCardNumberUseCase(cardNumber, cardConstraints.cardNumberLength)

                    _state.value = state.value.copy(
                        cardType = cardType,
                        cardConstraints = cardConstraints,
                        cvv = if (shouldDropCvv) "" else state.value.cvv,
                        isValidCardNumber = isValidCardNumber,
                    )
                }
        }

        viewModelScope.launch {
            getAgreement().collect {
                _state.value = state.value.copy(agreementText = it)
            }
        }


    }

    fun onErrorShown() {
        _state.value = state.value.copy(
            transactionError = null,
            errorMsg = null,
        )
    }

    fun onCardNumberChanged(cardNumber: String) {
        _state.value = state.value.copy(
            cardNumber = cardNumber,
        )
    }

    fun onExpiryDateChange(year: String) {
        require(year.isDigitsOnly() && year.length <= 4) {
            "Expiry date must be length of 4 and digits only"
        }
        _state.value = state.value.copy(
            expiryDate = year,
        )
    }

    fun onCardHolderNameChange(name: String) {
        _state.value = state.value.copy(
            cardHolderName = name,
        )
    }

    fun onCvvChange(cvv: String) {
        _state.value = state.value.copy(
            cvv = cvv,
        )
    }

    fun onZipCodeChange(zip: String) {
        _state.value = state.value.copy(
            zipCode = zip,
        )
    }

    private fun CardDetailsState.getCard(): CardDetails {
        val month = expiryDate.take(2)
        val year = expiryDate.drop(2)


        return CardDetails(
            number = cardNumber,
            month = month,
            year = year,
            cvv = cvv,
            holderName = cardHolderName,
            billingAddressZip = zipCode,
        )
    }

    fun onProceed(savedCardToken : String?) {
        viewModelScope.launch {
            val state = state.value

            if (savedCardToken.equals("0")) {
                val zipCode = state.zipCode.uppercase()
                if (!isZipValid(zipCode)) {
                    _state.value = state.copy(
                        transactionError = "Zip code is invalid",
                    )
                    return@launch
                }
            }
            val cardDetails = state.getCard()
            val amountDetails = state.amountDetails!!

            _state.value = state.copy(
                loading = true,
//                navigateNext = true
            )
                performTransaction(
                    card = cardDetails,
                    amount = amountDetails.order,
                    tips = amountDetails.tips,
                    tipsType = amountDetails.tipsType,
                    cardFee = state.convenienceCharge!!.toDouble(),
                    cardType = state.cardType.name,
                    amountPlusFee = state.amountPlusFee!!.toDouble(),
                    cardToken = savedCardToken,
                    saveCard = state.saveCard


                ).fold(
                    onSuccess = {
                        _state.value = state.copy(
                            loading = false,
                            transactionError = null,
                            navigateNext = true,
                        )
                    },
                    onFailure = {
                        if (!it.isNetworkException()) {
                            analytics.trackCustomAction(
                                name = Actions.failedTransaction,
                                attributes = mapOf(
                                    "failure_reason" to it.message,
                                ),
                            )
                        }

                        _state.value = state.copy(
                            loading = false,
                            transactionError = it.displayableErrorMessage(
                                stringResources,
                                defaultMessageResId = R.string.error_transaction_failed,
                            ),
                        )
                    },
                )
        }
    }

    fun onProceedWithTips(amount:Money, cardToken:String) {
        viewModelScope.launch {
            val state = state.value

            if (cardToken.equals("0")) {
                val zipCode = state.zipCode.uppercase()
                if (!isZipValid(zipCode)) {
                    _state.value = state.copy(
                        transactionError = "Zip code is invalid",
                    )
                    return@launch
                }
            }

            AppState1.cardDetails = state.getCard()
            AppState1.cardFee = state.convenienceCharge!!.toDouble()
            AppState1.amountPlusFee = state.amountPlusFee
            AppState1.cardType = state.cardType.name
            _state.value = state.copy(
                loading = true,
//                navigateNext = true
            )

            withContext(Dispatchers.IO) {
//                val signature = state.value.signature!!
//                saveSignature(signature)
                val amountMoney = amount
                amountMoney?.let { setAmount(it) }
//                _state.value = state.copy(
//                    navigateNext = true,
//                )

                amount?.let {
                    performTransaction(
                        card = AppState1.cardDetails!!,
                        amount = it,
                        tips = AppState1.tips.toMoney(),
                        tipsType = "",
                        cardType = AppState1.cardType,
                        cardFee = AppState1.cardFee!!,
                        amountPlusFee = AppState1.amountPlusFee!!.toDouble(),
                        cardToken = cardToken,
                        saveCard = state.saveCard

                    ).fold(
                        onSuccess = {
                            _state.value = state.copy(
                                navigateNext = true,
                                loading = false
                            )
                        },
                        onFailure = {
                            _state.value = state.copy(
                                navigateNext = false,
                                loading = false,
                                errorMsg = it.message.toString()
                            )
                        },
                    )
                }
            }
        }
    }

    fun onNavigateConsumed() {
        _state.value = state.value.copy(
            navigateNext = false,
        )
    }

    fun saveCardChecked(checked:Boolean) {
        _state.value = state.value.copy(
            saveCard = checked,
        )
    }

    companion object {
        fun isZipValid(zipCode: String) =
            zipCode.matches(usZipRegex) || zipCode.matches(canadaZipRegex) || zipCode.matches(
                anyZipRegex)

        val amexCardConstraints = CardConstraints(
            cardNumberLength = 15,
            cvvLength = 4,
        )

        val defaultCardConstraints = CardConstraints(
            cardNumberLength = 16,
            cvvLength = 3,
        )
    }
}

package com.ledgergreen.terminal.ui.card.details

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.text.isDigitsOnly
import com.ledgergreen.terminal.data.model.AmountDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.ui.card.details.CardDetailsViewModel.Companion.isZipValid

@Stable
@Immutable
data class CardDetailsState(
    val cardNumber: String,
    val expiryDate: String,
    val cardHolderName: String,
    val cvv: String,
    val zipCode: String,
    val cardType: CardType,
    val cardConstraints: CardConstraints,
    val amountDetails: AmountDetails?,
    val convenienceCharge: Money?,
    val total: Money?,
    val navigateNext: Boolean = false,
    val loading: Boolean = false,
    val transactionError: String? = null,
    val onErrorShown: () -> Unit,
    val isValidCardNumber: Boolean = false,
    val cardFee : Double,
    val amountPlusFee : Money?,
    val agreementText : String,
    val errorMsg : String? = null,
    val cardToken : String = "0",
    val saveCard : Boolean = false
) {
    val proceedAvailable: Boolean = run {
        val isCardNumberOk = cardNumber.length == cardConstraints.cardNumberLength
        val expiryDateOk = expiryDate.isDigitsOnly()
        val cvvOk = cvv.length == cardConstraints.cvvLength
        val convenienceChargeSet = convenienceCharge != null
//        val convenienceChargeSet = true
//        val totalSet = true
        val totalSet = total != null
        val zipCodeOk = isZipValid(zipCode)
        val validCardNumber = isValidCardNumber
        val isCardTokenNotAvailable = cardToken.equals("0")

        isCardNumberOk && expiryDateOk && cvvOk && convenienceChargeSet && totalSet && zipCodeOk
            && validCardNumber&& isCardTokenNotAvailable
    }

    val showWrongCardNumber: Boolean get() = cardNumber.isNotEmpty() && !isValidCardNumber
}

@Immutable
data class CardConstraints(
    val cardNumberLength: Int,
    val cvvLength: Int,
)

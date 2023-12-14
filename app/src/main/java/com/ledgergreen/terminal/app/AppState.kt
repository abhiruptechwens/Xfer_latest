package com.ledgergreen.terminal.app

import com.ledgergreen.terminal.data.model.AuthenticationState
import com.ledgergreen.terminal.data.model.CardDetails
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.network.model.Brand
import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.WalletDetails
import com.ledgergreen.terminal.idle.IdleLocker

data class AppState(
    val authenticationState: AuthenticationState,
    val brand: Brand?,
)

data class AppLockState(
    val timeToAutolockMs: Long? = null,
) {
    val showAutolockDialog
        get() : Boolean =
            timeToAutolockMs?.let { it <= IdleLocker.autolockDialogDurationMs } ?: false
}

object AppState1 {

    var lastVersionCode: Int = 1
    var savedCardDetails: Card? = null
    var balanceAmount : Money? = null
    var totalAmount : Money? = null
    var cardFee : Double? = null
    var amountPlusFee : Money? = null
    var cardType : String = ""
    var balanceUpdate: String = ""
    lateinit var inputAmount: String
    var cardDetails: CardDetails? = null
    var customerResponse: CustomerResponse? = null
    var walletDetails: WalletDetails? = null
    var tips :Double = 0.0
    var customerExtId = ""
    var dlError = ""
}

object PageState {
    var fromPage: String = ""
}

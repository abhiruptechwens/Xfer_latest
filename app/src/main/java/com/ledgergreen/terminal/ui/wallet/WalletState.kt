package com.ledgergreen.terminal.ui.wallet

import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.data.network.model.CustResponse
import com.ledgergreen.terminal.data.network.model.GetCustomer
import javax.annotation.concurrent.Immutable


@Immutable
data class WalletState(
    val customerId: String,
    val custResponse: CustResponse?,
    val isLoading: Boolean,
    val appInfo: String,
    val error: String?,
    val success: Boolean,
    val onErrorShown: () -> Unit,
    val setPage: (String) -> Unit,
)

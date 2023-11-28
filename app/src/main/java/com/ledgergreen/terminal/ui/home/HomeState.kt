package com.ledgergreen.terminal.ui.home

import androidx.compose.runtime.Immutable
import com.ledgergreen.terminal.data.model.UserInfo
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.data.network.model.WalletDetails

@Immutable
data class HomeState(
    val userInfo: UserInfo,
    val loading: Boolean,
    val registrationRequired: Boolean,
    val scannerStarted: Boolean,
    val customerResponse: CustomerResponse?,
    val walletResponse: WalletDetails?,
    val showScannerHint: Boolean,
    val errorMessage: String?,
    val onErrorShown: () -> Unit,
    val navigateNext: Boolean,
    val isDialogShown: Boolean,
)

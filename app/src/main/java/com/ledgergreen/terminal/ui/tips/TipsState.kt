package com.ledgergreen.terminal.ui.tips

import androidx.compose.runtime.Immutable
import com.ledgergreen.terminal.data.model.Money
import com.ledgergreen.terminal.data.model.Signature
import com.ledgergreen.terminal.ui.tips.domain.TipOption
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TipsState(
    val orderAmount: String,
    val tipsOptions: ImmutableList<TipOption>,
    val selectedTipOption: TipOption?,
    val tipCustom: String,
    val navigateNext: Boolean = false,
    val navigateNextToHomeScreen: Boolean = false,
    val eventSink: (TipEvent) -> Unit,
    val walletBalance : String,
    val accountNumber : String,
    val agreementText: String,
    val agreementAccepted: Boolean,
    val signature: Signature?,
    val customerExtId: String,
    val receiptId: String,
    val orderAmountAfterTransaction: String,
    val error: String?,
    val errorMsg: String?,
    val showTipAmount: Boolean,
){
    val proceedAvailable = signature != null && agreementAccepted
}

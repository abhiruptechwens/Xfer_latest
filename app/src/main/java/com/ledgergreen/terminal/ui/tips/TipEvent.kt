package com.ledgergreen.terminal.ui.tips

import com.ledgergreen.terminal.ui.tips.domain.TipOption

sealed class TipEvent {
    data class TipOptionSelected(val tipOption: TipOption) : TipEvent()
    data class CustomTipChanged(val amount: String) : TipEvent()
}

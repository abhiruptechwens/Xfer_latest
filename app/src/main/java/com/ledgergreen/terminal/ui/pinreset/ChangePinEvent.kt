package com.ledgergreen.terminal.ui.pinreset

sealed class ChangePinEvent {
    data class Pin1Changed(val pin: String) : ChangePinEvent()
    data class Pin2Changed(val pin: String) : ChangePinEvent()
    object ChangePin : ChangePinEvent()
    object ErrorShown : ChangePinEvent()
}

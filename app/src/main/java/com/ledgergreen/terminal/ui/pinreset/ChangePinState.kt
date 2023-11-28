package com.ledgergreen.terminal.ui.pinreset

data class ChangePinState(
    val pin1: String,
    val pin2: String,
    val loading: Boolean,
    val error: String?,
    val versionInfo: String,
    val eventSink: (ChangePinEvent) -> Unit,
)

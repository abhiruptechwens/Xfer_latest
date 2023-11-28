package com.ledgergreen.terminal.ui.common

sealed class BackPressSealed{

    object Idle : BackPressSealed()
    object InitialTouch : BackPressSealed()
}

package com.ledgergreen.terminal.ui.common

import java.math.RoundingMode

fun Double.round2Decimals() =
    this.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

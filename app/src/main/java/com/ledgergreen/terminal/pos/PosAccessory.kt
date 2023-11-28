package com.ledgergreen.terminal.pos

import com.ledgergreen.terminal.pos.beeper.Beeper
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.led.Led
import com.ledgergreen.terminal.scanner.Scanner

enum class PosVendor {
    Nexgo, MoreFun
}

interface PosAccessory {
    fun vendor(): PosVendor
    fun initialize()
    fun cardDetailsReader(): CardReader
    fun scanner(): Scanner
    fun beeper(): Beeper
    fun led(): Led
}

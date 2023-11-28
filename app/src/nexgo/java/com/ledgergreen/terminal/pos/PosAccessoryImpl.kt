package com.ledgergreen.terminal.pos

import com.ledgergreen.terminal.pos.beeper.Beeper
import com.ledgergreen.terminal.pos.beeper.BeeperImpl
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.card.NexgoCardReader
import com.ledgergreen.terminal.pos.led.Led
import com.ledgergreen.terminal.pos.led.LedImpl
import com.ledgergreen.terminal.pos.scanner.ScannerImpl
import com.ledgergreen.terminal.scanner.Scanner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PosAccessoryImpl @Inject constructor(
    private val nexgoEngineProvider: NexgoEngineProvider,
    private val nexgoCardReader: NexgoCardReader,
) : PosAccessory {
    override fun vendor(): PosVendor = PosVendor.Nexgo
    override fun initialize() = nexgoEngineProvider.initialize()
    override fun cardDetailsReader(): CardReader = nexgoCardReader
    override fun scanner(): Scanner = ScannerImpl(nexgoEngineProvider)
    override fun beeper(): Beeper = BeeperImpl(nexgoEngineProvider.beeper)
    override fun led(): Led = LedImpl(nexgoEngineProvider.ledDriver)
}

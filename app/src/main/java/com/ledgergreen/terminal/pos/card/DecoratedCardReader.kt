package com.ledgergreen.terminal.pos.card

import com.ledgergreen.terminal.pos.beeper.Beeper
import com.ledgergreen.terminal.pos.led.Led

class DecoratedCardReader(
    private val realCardReader: CardReader,
    beeper: Beeper,
    led: Led,
) : CardReader {

    private val internalListeners = listOf(
        BeeperCardReaderListener(beeper),
        LedCardReaderListener(led),
    )

    override fun start(listener: CardReaderEventListener) {
        realCardReader.start { event ->
            internalListeners.forEach { it(event) }

            listener(event)
        }
    }

    override fun stop() {
        realCardReader.stop()
    }
}

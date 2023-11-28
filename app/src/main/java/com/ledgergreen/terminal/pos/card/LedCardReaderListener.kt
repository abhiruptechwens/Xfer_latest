package com.ledgergreen.terminal.pos.card

import com.ledgergreen.terminal.pos.led.Led
import com.ledgergreen.terminal.pos.led.LedColor

class LedCardReaderListener(
    private val led: Led,
) : CardReaderEventListener {
    override fun invoke(event: CardReaderEvent) {
        // blue enabled when scanner is active
        // enable blue between Start and Stop.

        // green is active when emv process is active
        // between StartEmvProcess and either CardFound or EmvError
        when (event) {
            CardReaderEvent.Start -> {
                // enable blue
                toggleLed(readerStarted = true, emvInProgress = false)
            }

            CardReaderEvent.StartEmvProcess -> {
                // enable green
                toggleLed(readerStarted = true, emvInProgress = true)
            }

            is CardReaderEvent.ErrorMessage -> {
                if (event.stopped) {
                    // disable all
                    toggleLed(readerStarted = false, emvInProgress = false)
                }
            }

            is CardReaderEvent.CardFound,
            CardReaderEvent.Stop,
            -> {
                // disable all
                toggleLed(readerStarted = false, emvInProgress = false)
            }

        }
    }

    private fun toggleLed(readerStarted: Boolean, emvInProgress: Boolean) {
        led.setLight(LedColor.Blue, readerStarted)
        led.setLight(LedColor.Green, emvInProgress)
    }
}

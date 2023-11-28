package com.ledgergreen.terminal.pos.card

import com.ledgergreen.terminal.pos.beeper.Beeper

class BeeperCardReaderListener(
    private val beeper: Beeper,
) : CardReaderEventListener {
    override fun invoke(event: CardReaderEvent) {
        when (event) {
            is CardReaderEvent.CardFound -> beeper.beepSuccess()
            is CardReaderEvent.ErrorMessage -> beeper.beepFailure()
            else -> {
                // no-op
            }
        }
    }
}

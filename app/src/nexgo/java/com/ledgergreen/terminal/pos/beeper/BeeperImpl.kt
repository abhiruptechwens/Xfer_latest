package com.ledgergreen.terminal.pos.beeper

import com.nexgo.oaf.apiv3.device.beeper.Beeper as RealBeeper

class BeeperImpl(
    private val beeper: RealBeeper,
) : Beeper {
    override fun beepSuccess() {
        beeper.beep(5)
    }

    override fun beepFailure() {
        beeper.beep(10)
        beeper.beep(10)
        beeper.beep(10)
    }
}

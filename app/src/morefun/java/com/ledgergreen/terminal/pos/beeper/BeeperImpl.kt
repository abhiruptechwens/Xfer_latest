package com.ledgergreen.terminal.pos.beeper

import com.ledgergreen.terminal.pos.MfService
import com.morefun.yapi.device.beeper.BeepModeConstrants

class BeeperImpl(private val mfService: MfService) : Beeper {
    override fun beepSuccess() {
        mfService.beeper.beep(BeepModeConstrants.SUCCESS)
    }

    override fun beepFailure() {
        mfService.beeper.beep(BeepModeConstrants.ERROR)
    }
}

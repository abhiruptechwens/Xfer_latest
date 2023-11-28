package com.ledgergreen.terminal.pos.led

import com.ledgergreen.terminal.pos.MfService
import com.morefun.yapi.device.led.LEDLightConstrants

class LedImpl(private val mfService: MfService) : Led {

    override fun setLight(color: LedColor, enable: Boolean) {
        mfService.led.setLed(color.toMfColor(), enable)
    }

    private fun LedColor.toMfColor(): Int = when (this) {
        LedColor.Red -> LEDLightConstrants.RED
        LedColor.Green -> LEDLightConstrants.GREEN
        LedColor.Blue -> LEDLightConstrants.BLUE
        LedColor.Yellow -> LEDLightConstrants.YELLOW
    }
}

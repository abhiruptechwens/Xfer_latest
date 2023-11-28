package com.ledgergreen.terminal.pos.led

import com.nexgo.oaf.apiv3.device.led.LEDDriver
import com.nexgo.oaf.apiv3.device.led.LightModeEnum

class LedImpl(private val ledDriver: LEDDriver) : Led {
    override fun setLight(color: LedColor, enable: Boolean) {
        ledDriver.setLed(color.toNexgoColor(), enable)
    }

    private fun LedColor.toNexgoColor() = when (this) {
        LedColor.Red -> LightModeEnum.RED
        LedColor.Green -> LightModeEnum.GREEN
        LedColor.Blue -> LightModeEnum.BLUE
        LedColor.Yellow -> LightModeEnum.YELLOW
    }
}

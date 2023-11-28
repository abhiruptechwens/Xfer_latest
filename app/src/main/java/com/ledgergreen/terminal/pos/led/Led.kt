package com.ledgergreen.terminal.pos.led

enum class LedColor {
    Red, Green, Blue, Yellow
}

interface Led {
    fun setLight(color: LedColor, enable: Boolean)
}

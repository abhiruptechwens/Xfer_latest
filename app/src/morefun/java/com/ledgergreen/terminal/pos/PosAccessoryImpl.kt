package com.ledgergreen.terminal.pos

import com.ledgergreen.terminal.pos.beeper.Beeper
import com.ledgergreen.terminal.pos.beeper.BeeperImpl
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.cardreader.MfCardReader
import com.ledgergreen.terminal.pos.led.Led
import com.ledgergreen.terminal.pos.led.LedImpl
import com.ledgergreen.terminal.pos.scanner.ScannerImpl
import com.ledgergreen.terminal.scanner.Scanner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PosAccessoryImpl @Inject constructor(
    private val mfService: MfService,
) : PosAccessory {

    // MF SDK service is not stable and often crashes
    // DO NOT store mf classes as a class property
    // val iccCardReader = mfService.iccCardReader
    // …
    // iccCardReader.doSomething()
    // DO ask mfService every time you need something from MF
    // mfService.iccCardReader.doSomething()
    // dirty but reliable

    override fun vendor(): PosVendor = PosVendor.MoreFun

    override fun initialize() {
        mfService.initialize()
    }

    override fun cardDetailsReader(): CardReader = MfCardReader(mfService)

    override fun scanner(): Scanner = ScannerImpl(mfService)

    override fun beeper(): Beeper = BeeperImpl(mfService)

    override fun led(): Led = LedImpl(mfService)

    companion object {
        const val ACTION_MOREFUN_SERVICE = "com.morefun.ysdk.service"
        const val PACKAGE_MOREFUN = "com.morefun.ysdk"
    }
}

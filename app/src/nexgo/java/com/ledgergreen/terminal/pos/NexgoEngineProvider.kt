package com.ledgergreen.terminal.pos

import android.content.Context
import com.ledgergreen.terminal.pos.card.EmvUtils
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.device.beeper.Beeper
import com.nexgo.oaf.apiv3.device.led.LEDDriver
import com.nexgo.oaf.apiv3.device.reader.CardReader
import com.nexgo.oaf.apiv3.device.scanner.Scanner
import com.nexgo.oaf.apiv3.device.scanner.Scanner2
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NexgoEngineProvider @Inject constructor(@ApplicationContext private val context: Context) {
    lateinit var deviceEngine: DeviceEngine
    lateinit var scanner: Scanner
    lateinit var scanner2: Scanner2
    lateinit var emvHandler: EmvHandler2
    lateinit var cardReader: CardReader
    lateinit var beeper: Beeper
    lateinit var ledDriver: LEDDriver

    fun initialize() {
        deviceEngine = APIProxy.getDeviceEngine(context)
        scanner = deviceEngine.scanner
        scanner2 = deviceEngine.scanner2
        emvHandler = deviceEngine.getEmvHandler2("app1").apply {
            initEmvHandler(this)
        }
        cardReader = deviceEngine.cardReader
        beeper = deviceEngine.beeper
        ledDriver = deviceEngine.ledDriver
    }

    fun initEmvHandler(emvHandler2: EmvHandler2) = with(emvHandler2) {
        delAllAid()
        delAllCapk()
        val emvUtils = EmvUtils(context)
        setAidParaList(emvUtils.getAidList())
        setCAPKList(emvUtils.getAidList())
    }
}

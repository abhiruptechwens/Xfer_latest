package com.ledgergreen.terminal.pos.scanner

import androidx.core.os.bundleOf
import com.ledgergreen.terminal.pos.MfService
import com.ledgergreen.terminal.scanner.Scanner
import com.ledgergreen.terminal.scanner.ScannerCallback
import com.ledgergreen.terminal.scanner.ScannerMethod
import com.morefun.yapi.ServiceResult
import com.morefun.yapi.device.beeper.BeepModeConstrants
import com.morefun.yapi.device.scanner.OnScannedListener
import com.morefun.yapi.device.scanner.ScannerConfig
import com.morefun.yapi.device.scanner.ZebraParam
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class ScannerImpl @Inject constructor(
    private val mfService: MfService,
) : Scanner {

    override val scannerMethod: ScannerMethod = ScannerMethod.Infrared

    override fun start(callback: ScannerCallback) {
        Timber.d("MfScanner start scanner")
        mfService.scanner.initScanner(
            bundleOf(
                ScannerConfig.ZEBRA_PARAM to
                    arrayListOf(
                        ZebraParam(
                            ScannerConfig.ZerbaParamNum.PICKLIST_MODE,
                            ScannerConfig.ZebraParamVal.SUPP_AUTOD,
                        ),
                        // enables MRZ for MRD documents
                        ZebraParam(681.toShort(), 1.toByte()),
                        ZebraParam(685.toShort(), 11.toByte()),
                        // enables PDF417 for Driver license
                        ZebraParam(15.toShort(), 1.toByte()),
                        ZebraParam(277.toShort(), 1.toByte()),
                    ),
                ScannerConfig.COMM_SCANNER_TYPE to 0,
            ),
        )

        mfService.scanner.stopScan() // ¯\_(ツ)_/¯
        mfService.scanner.startScan(
            Scanner.scannerTimeoutSec * 1000,
            object : OnScannedListener.Stub() {
                override fun onScanResult(retCode: Int, data: ByteArray?) {
                    when (retCode) {
                        ServiceResult.Success -> {
                            Timber.v("MfDocScanner result")
                            data?.let {
                                callback.onResult(String(data))
                            }
                            mfService.beeper.beep(BeepModeConstrants.SUCCESS)
                        }

                        ServiceResult.Scanner_CALLBACK_FAIL -> {
                            Timber.v("MfDocScanner failed Scanner_CALLBACK_FAIL")
                            callback.onCancel()
                        }

                        else -> {
                            Timber.w("MfDocScanner retCode $retCode")
                        }
                    }
                }
            },
        )
    }

    override fun stop() {
        Timber.d("MfScanner stop")
        mfService.scanner.stopScan()
    }
}

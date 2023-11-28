package com.ledgergreen.terminal.pos.scanner

import androidx.core.os.bundleOf
import com.ledgergreen.terminal.pos.NexgoEngineProvider
import com.ledgergreen.terminal.scanner.Scanner
import com.ledgergreen.terminal.scanner.ScannerCallback
import com.ledgergreen.terminal.scanner.ScannerMethod
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.scanner.OnScannerListener
import com.nexgo.oaf.apiv3.device.scanner.ScannerCfgEntity
import com.nexgo.oaf.apiv3.device.scanner.SymbolEnum
import javax.inject.Inject
import timber.log.Timber

class ScannerImpl @Inject constructor(
    private val deviceEngineProvider: NexgoEngineProvider,
) : Scanner {
    override val scannerMethod: ScannerMethod = ScannerMethod.Camera

    override fun start(callback: ScannerCallback) {
        Timber.d("NexgoScanner start scanner")
        deviceEngineProvider.scanner.stopScan()

        val cfgEntity = ScannerCfgEntity().apply {
            isAutoFocus = true
            isUsedFrontCcd = false
            isBulkMode = false
            interval = 500
            symbolEnumList = listOf(SymbolEnum.PDF417)
            customBundle = bundleOf(
                "hideFrame" to true,
            )
        }

        val innerListener = object : OnScannerListener {
            override fun onInitResult(resultCode: Int) {
                when (resultCode) {
                    SdkResult.Success -> {
                        Timber.d("Scanner Init Success")
                        deviceEngineProvider.scanner.startScan(Scanner.scannerTimeoutSec, this)
                    }

                    else -> {
                        Timber.w("Scanner Failed to Init: $resultCode (or user pressed back)")
                        deviceEngineProvider.scanner.closeDecoder()
                        deviceEngineProvider.scanner.stopScan()
                        // if user presses back button, it will fail with -1 (Fail)
                        // it is impossible to determine if it was an error or back press
                        // treat it as cancel by default
                        callback.onCancel()
                    }
                }
            }

            override fun onScannerResult(resultCode: Int, result: String?) {
                when (resultCode) {
                    SdkResult.Success -> {
                        if (result != null) {
                            callback.onResult(result)
                        } else {
                            callback.onCancel()
                        }
                    }

                    SdkResult.Scanner_Customer_Exit -> {
                        callback.onCancel()
                    }

                    SdkResult.Scanner_Other_Error -> {
                        Timber.w("Got Scanner Error: OtherError")
                        callback.onError("Scanner error. Code $resultCode")
                    }

                    else -> {
                        Timber.w("Other general error.")
                        callback.onError("Scanner error. Code $resultCode")
                    }
                }
                deviceEngineProvider.scanner.stopScan()
            }
        }

        deviceEngineProvider.scanner.initScanner(cfgEntity, innerListener)
    }

    override fun stop() {
        Timber.d("NexgoScanner stop scanner")
        deviceEngineProvider.scanner.stopScan()
    }
}

package com.ledgergreen.terminal.scanner

enum class ScannerMethod {
    Camera, Infrared
}

interface Scanner {
    val scannerMethod: ScannerMethod
    fun start(callback: ScannerCallback)
    fun stop()

    companion object {
        const val scannerTimeoutSec = 30
    }
}

interface ScannerCallback {
    fun onResult(result: String)
    fun onError(error: String)
    fun onCancel()
}

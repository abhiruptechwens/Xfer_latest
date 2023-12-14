package com.ledgergreen.terminal.scanner

import android.content.Context

interface IdScanner {
    val scannerMethod: ScannerMethod
    suspend fun scan(): Document?
}

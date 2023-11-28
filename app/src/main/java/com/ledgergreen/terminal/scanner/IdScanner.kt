package com.ledgergreen.terminal.scanner

interface IdScanner {
    val scannerMethod: ScannerMethod
    suspend fun scan(): Document?
}

package com.ledgergreen.terminal.data.network.exception

open class RequestException(message: String, val fatal: Boolean = true) : Exception(message)

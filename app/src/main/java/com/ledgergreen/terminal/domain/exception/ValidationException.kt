package com.ledgergreen.terminal.domain.exception

class ValidationException(message: String) : Exception(message)

fun validationError(message: String): Nothing = throw ValidationException(message)

package com.ledgergreen.terminal.ui.common

import java.util.Locale

fun String.titleCase() =
    this.split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

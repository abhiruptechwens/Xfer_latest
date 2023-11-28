package com.ledgergreen.terminal.ui.common

import androidx.compose.ui.graphics.vector.ImageVector
import com.ledgergreen.terminal.ui.common.ledgergreenicons.GreenledgerLogo
import com.ledgergreen.terminal.ui.common.ledgergreenicons.IcBackspace
import com.ledgergreen.terminal.ui.common.ledgergreenicons.IcScan
import com.ledgergreen.terminal.ui.common.ledgergreenicons.IcScanButton

object LedgerGreenIcons

private var allIcons: List<ImageVector>? = null

val LedgerGreenIcons.AllIcons: List<ImageVector>
    get() {
        if (allIcons != null) {
            return allIcons!!
        }
        allIcons = listOf(IcBackspace, GreenledgerLogo, IcScanButton, IcScan)
        return allIcons!!
    }

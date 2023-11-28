package com.ledgergreen.terminal.ui.card.reader

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.data.network.model.Card
import com.ledgergreen.terminal.pos.card.CardDetails

@Stable
@Immutable
data class CardReaderState(
    val cardReaderResult: CardDetails?,
    val message: String? = null,
    val onMessageShown: () -> Unit,
    val navigateNext: Boolean,
    val onNavigateNextConsumed: () -> Unit,
    val savedCards: MutableList<Card>?,
    val isLoading: Boolean,
    val success: Boolean,
)

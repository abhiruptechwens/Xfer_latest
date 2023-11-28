package com.ledgergreen.terminal.pos.card

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class CardReaderEvent {
    object Start : CardReaderEvent()
    object StartEmvProcess : CardReaderEvent()

    /** Means that card has found read and process is finished */
    data class CardFound(val result: CardDetails) : CardReaderEvent()
    data class ErrorMessage(
        val message: String,
        val stopped: Boolean,
    ) : CardReaderEvent()

    object Stop : CardReaderEvent()
}

fun interface CardReaderEventListener {
    operator fun invoke(event: CardReaderEvent)
}

interface CardReader {
    fun start(listener: CardReaderEventListener)
    fun stop()
}

fun CardReader.start(): Flow<CardReaderEvent> = callbackFlow {
    start { cardDetailsReaderEvent -> trySend(cardDetailsReaderEvent) }

    awaitClose {
        stop()
    }
}

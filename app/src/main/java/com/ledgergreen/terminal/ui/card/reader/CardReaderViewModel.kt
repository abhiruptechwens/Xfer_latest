package com.ledgergreen.terminal.ui.card.reader

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.AppState1.savedCards
import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.idle.IdleLocker
import com.ledgergreen.terminal.monitoring.Actions
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.pos.card.CardDetails
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.card.CardReaderEvent
import com.ledgergreen.terminal.pos.card.start
import com.ledgergreen.terminal.ui.card.reader.domain.DeleteCardUseCase
import com.ledgergreen.terminal.ui.card.reader.domain.SaveCardReaderResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltViewModel
class CardReaderViewModel @Inject constructor(
    private val cardReader: CardReader,
    private val saveCardReaderResult: SaveCardReaderResultUseCase,
    private val deleteCard: DeleteCardUseCase,
    private val analytics: Analytics,
    private val transactionCache: TransactionCache,
    private val idleLocker: IdleLocker,
) : ViewModel() {

    private var readerJob: Job? = null

    private val _state = MutableStateFlow(
        CardReaderState(
            savedCards = transactionCache.custWalletResponse.value?.card,
            cardReaderResult = null,
            onMessageShown = ::onMessageShown,
            navigateNext = false,
            onNavigateNextConsumed = ::onNavigationConsumed,
            isLoading = false,
            success = false,
        ),
    )

    val state: StateFlow<CardReaderState> = _state

    fun onStart() {
        startCardScanner()
    }

    fun onStop() {
        readerJob?.cancel()
    }

    fun onEnterManually() {
        Timber.i("Enter manually")
        saveCardReaderResult(cardReaderResult = null)
        _state.value = state.value.copy(
            navigateNext = true,
        )
    }

    fun onDeletePressed(cardToken: String, index:Int){

        _state.value = _state.value.copy(
            isLoading = true
        )
        viewModelScope.launch {

                deleteCard(
                cardToken = cardToken,
            ).fold(
                onSuccess = {
                    println("Success")
                    transactionCache.custWalletResponse.value?.card?.removeAt(index)
                    _state.value = state.value.copy(
                        savedCards = transactionCache.custWalletResponse.value?.card,
                        isLoading = false,
                        success = true,
                    )
                },
                onFailure = {
//                    transactionCache.custWalletResponse.value?.card?.removeAt(index)
                    _state.value = state.value.copy(
//                        savedCards = transactionCache.custWalletResponse.value?.card,
                        isLoading = false,
                        success = false
                    )
                },
            )
        }

    }

    private fun startCardScanner() {
        readerJob?.cancel()
        readerJob = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                cardReader.start()
                    .collect { event ->
                        idleLocker.onActivityDetected()

                        when (event) {
                            is CardReaderEvent.CardFound -> onCardFound(event.result)

                            is CardReaderEvent.ErrorMessage -> {
                                _state.value = state.value.copy(
                                    message = event.message,
                                )
                                if (event.stopped) {
                                    Timber.d("Error stopped CardReader. Restart")
                                    delay(500L)
                                    startCardScanner()
                                }
                            }

                            else -> {
                                // no-op; additional feedback on UI can be added here
                            }
                        }
                    }
            }
        }
    }
    fun onDialogClosed(){
        _state.value = state.value.copy(
            success = false,
        )
    }

    private fun onCardFound(cardResult: CardDetails) {
        Timber.i("Card data read")
        analytics.trackCustomAction(
            name = Actions.cardScanned,
            attributes = mapOf("method" to cardResult.method),
        )

        saveCardReaderResult(cardResult)

        _state.value = state.value.copy(
            cardReaderResult = cardResult,
            navigateNext = true,
        )
    }

    private fun onNavigationConsumed() {
        _state.value = state.value.copy(
            navigateNext = false,
        )
    }

    private fun onMessageShown() {
        _state.value = state.value.copy(
            message = null,
        )
    }
}

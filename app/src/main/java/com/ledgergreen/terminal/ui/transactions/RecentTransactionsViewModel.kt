package com.ledgergreen.terminal.ui.transactions

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.data.network.model.TransactionStatus
import com.ledgergreen.terminal.monitoring.Analytics
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.ui.common.failure.displayableErrorMessage
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import com.ledgergreen.terminal.ui.transactions.domain.FilterTransactionsUseCase
import com.ledgergreen.terminal.ui.transactions.domain.GetRecentTransactionsUseCase
import com.ledgergreen.terminal.ui.transactions.domain.TransactionsFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecentTransactionsViewModel @Inject constructor(
    private val getRecentTransactions: GetRecentTransactionsUseCase,
    private val stringResources: StringResources,
    private val filterTransactionsUseCase: FilterTransactionsUseCase,
    private val analytics: Analytics,
) : ViewModel() {

    private val _state = MutableStateFlow(
        RecentTransactionsState(
            selectedOption = "All",
            allTransactions = emptyList(),
            transactions = persistentListOf(),
            searchQuery = "",
            filters = (TransactionStatus.values().map {
                TransactionsFilter.TransactionStatusFilter(it, false)
            } + TransactionsFilter.CreatedByMeFilter(false))
                .toPersistentList(),
            loading = false,
            error = null,
            eventSink = ::eventSink,
        ),
    )

    val state: StateFlow<RecentTransactionsState> = _state

    init {
        onRefresh()
    }

    fun updateSelectedOption(option: String) {
        _state.value = _state.value.copy(selectedOption = option)
        onRefresh()
    }

    private fun eventSink(event: RecentTransactionsEvent) {
        when (event) {
            is RecentTransactionsEvent.FilterSelected -> onFilterChange(event.filter)
            is RecentTransactionsEvent.SearchChanged -> onSearchChanged(event.searchQuery)
            is RecentTransactionsEvent.optionSelected -> updateSelectedOption(event.option)
            RecentTransactionsEvent.Refresh -> onRefresh()
            RecentTransactionsEvent.OnErrorShown -> onErrorShown()
        }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true,
            )

            getRecentTransactions(state.value.selectedOption.lowercase())
                .fold(
                    onSuccess = { transactions ->
                        _state.value = state.value.copy(
                            allTransactions = transactions,
                            transactions = filterTransactions(transactions = transactions)
                                .toPersistentList(),
                            loading = false,
                        )
                    },
                    onFailure = {
                        _state.value = state.value.copy(
                            loading = false,
                            error = it.displayableErrorMessage(
                                stringResources,
                                defaultMessageResId = R.string.error_transaction_failed,
                            ),
                        )
                    },
                )
        }
    }

    private fun filterTransactions(
        transactions: List<Transaction> = state.value.allTransactions,
        searchQuery: String = state.value.searchQuery,
        filters: List<TransactionsFilter> = state.value.filters,
    ): List<Transaction> = filterTransactionsUseCase(
        transactions, searchQuery, filters,
    )

    private fun onSearchChanged(searchQuery: String) {
        _state.value = state.value.copy(
            searchQuery = searchQuery,
            transactions = filterTransactions(
                searchQuery = searchQuery,
            ).toPersistentList(),
        )
    }

    private fun onFilterChange(filter: TransactionsFilter) {
        val currentState = state.value
        val newFilters = currentState.filters.toMutableList()
        // replace filter at index with inverted one
        val filterIndex = newFilters.indexOf(filter)
        newFilters[filterIndex] = filter.toggle()

        _state.value = currentState.copy(
            filters = newFilters.toPersistentList(),
            transactions = filterTransactions(filters = newFilters).toPersistentList(),
        )

        analytics.trackTap(
            Clicks.transactionListFilter,
            mutableMapOf(
                "filter_type" to when (filter) {
                    is TransactionsFilter.CreatedByMeFilter -> "created_by_me"
                    is TransactionsFilter.TransactionStatusFilter -> "transaction_status"
                },
                "selected" to !filter.enabled,
            ).apply {
                if (filter is TransactionsFilter.TransactionStatusFilter) {
                    this["transaction_status"] = filter.status.toString()
                }
            },
        )
    }

    private fun onErrorShown() {
        _state.value = state.value.copy(error = null)
    }
}

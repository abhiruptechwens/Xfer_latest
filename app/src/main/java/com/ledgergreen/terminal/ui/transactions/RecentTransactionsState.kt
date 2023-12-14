package com.ledgergreen.terminal.ui.transactions

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.ui.transactions.domain.TransactionsFilter
import kotlinx.collections.immutable.ImmutableList

@Immutable
@Stable
data class RecentTransactionsState(
    val allTransactions: List<Transaction>,
    val transactions: ImmutableList<Transaction>,
    val searchQuery: String,
    val filters: ImmutableList<TransactionsFilter>,
    val loading: Boolean,
    val error: String?,
    val selectedOption: String,
    val pageNumber: Int,
    val eventSink: (RecentTransactionsEvent) -> Unit,
    val totalPages: Int,
//    val updatedTransaction: ImmutableList<Transaction>
)

sealed class RecentTransactionsEvent {
    data class FilterSelected(val filter: TransactionsFilter) : RecentTransactionsEvent()
    data class optionSelected(val option: String) : RecentTransactionsEvent()
    data class SearchChanged(val searchQuery: String) : RecentTransactionsEvent()
    object Refresh : RecentTransactionsEvent()
    object OnErrorShown : RecentTransactionsEvent()
}


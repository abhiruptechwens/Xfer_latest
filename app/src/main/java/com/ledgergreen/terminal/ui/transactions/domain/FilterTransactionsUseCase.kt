package com.ledgergreen.terminal.ui.transactions.domain

import com.ledgergreen.terminal.data.TransactionCache
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.data.network.model.TransactionStatus
import javax.inject.Inject

sealed class TransactionsFilter {
    abstract val enabled: Boolean

    abstract fun toggle(): TransactionsFilter

    data class TransactionStatusFilter(
        val status: TransactionStatus,
        override val enabled: Boolean,
    ) : TransactionsFilter() {
        override fun toggle(): TransactionsFilter = TransactionStatusFilter(status, !enabled)
    }

    data class CreatedByMeFilter(
        override val enabled: Boolean,
    ) : TransactionsFilter() {
        override fun toggle(): TransactionsFilter = CreatedByMeFilter(!enabled)
    }
}

class FilterTransactionsUseCase @Inject constructor(
    private val transactionCache: TransactionCache,
) {
    operator fun invoke(
        transactions: List<Transaction>,
        searchQuery: String,
        filters: List<TransactionsFilter>,
    ): List<Transaction> {
        val statusFilters = filters.filterIsInstance<TransactionsFilter.TransactionStatusFilter>()
        val byMeFilter = filters.filterIsInstance<TransactionsFilter.CreatedByMeFilter>().first()

        return transactions
            .filter { filterByStatus(statusFilters, it) }
            .filter { filterMyTransactions(byMeFilter, it) }
            .filter { search(searchQuery, it) }
    }

    private fun filterByStatus(
        statusFilters: List<TransactionsFilter.TransactionStatusFilter>,
        transaction: Transaction,
    ): Boolean {
        // if all disabled -> show all
        if (statusFilters.all { !it.enabled }) {
            return true
        }

        // any transaction that has status
        return statusFilters
            .filter { it.enabled }
            .any { statusFilter ->
                transaction.status == statusFilter.status
            }
    }

    private fun filterMyTransactions(
        byMeFilter: TransactionsFilter.CreatedByMeFilter,
        transaction: Transaction,
    ): Boolean = if (byMeFilter.enabled) {
        transaction.associateId.toLong() == transactionCache.pinResponse.value!!.userId
    } else {
        true
    }

    private fun search(
        searchQuery: String,
        transaction: Transaction,
    ): Boolean = if (searchQuery.isBlank()) {
        true
    } else {
        transaction.customerName.contains(searchQuery, ignoreCase = true)
            || transaction.associate?.contains(searchQuery, ignoreCase = true) == true
            || transaction.amount.toString().contains(searchQuery, ignoreCase = true)
    }
}

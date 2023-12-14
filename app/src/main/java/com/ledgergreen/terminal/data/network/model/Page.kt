package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val totalItems: Int,
    val records: List<Transaction>,
    val totalPages: Int,
    val currentPage: Int,
    val page: Int,
    val size: Int,
)

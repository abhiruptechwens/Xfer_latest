package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val totalItems: Int,
    val records: List<T>,
    val totalPages: Int,
    val currentPage: Int,
    val page: Int,
    val size: Int,
)

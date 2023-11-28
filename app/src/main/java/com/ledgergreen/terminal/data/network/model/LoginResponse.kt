package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    // uuid
    val id: String,
    val terminal: Terminal,
    val name: String,
    val message: String,
    val role: String,
    val store: Store,
    val user: User,
    @SerialName("company_id")
    val companyId: Long,
    @SerialName("company_external_id")
    val companyExternalId: String,
    val token: String,
    val brand: Brand,
    @SerialName("reset_password_required")
    val resetPasswordRequired: Boolean = false,
)

@Serializable
data class Terminal(
    val terminalId: String,
    val id: Long,
    val name: String,
    val slug: String?,
)

@Serializable
data class Store(
    val storeId: Long,
)

@Serializable
data class User(
    val id: Long,
    val ids: String,
)

@Serializable
data class Brand(
    val slug: String,
    val vendorName: String,
    @SerialName("agreement_text")
    val agreementText: String,
    @SerialName("authorised_text")
    val authorizedText: String,
    @SerialName("logo_url")
    val logoUrl: String,
)

package com.ledgergreen.terminal.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinResponse(
    val status: Boolean,
    /** uuid */
    val id: String,
    @SerialName("user_id")
    val userId: Long,
    val permission: List<String>,
    val storeId: String,
    val role: String,
    val terminalId: String,
    val name: String,
    val store: StoreDetails,
    val tips: List<TipOption>?,
    @SerialName("pin_reset_required")
    val pinResetRequired: Boolean,
)

@Serializable
data class StoreDetails(
    val id: Long,
    val storeId: Long,
    @SerialName("store_name")
    val name: String,
    val state: String,
    @SerialName("external_id")
    val externalId: String,
    /** uuid */
    val company: String,
    val companyId: Long,
    val descriptor: String,
    val slug: String,
    @SerialName("account_number")
    val accountNumber: String,
    @SerialName("show_tip_amount")
    val showTipAmount: Boolean,
)

@Serializable
enum class TipsType {
    @SerialName("radio")
    Radio,

    @SerialName("inputtext")
    InputText,
}

@Serializable
enum class AmountType {
    @SerialName("percentage")
    Percentage,

    @SerialName("flat")
    Flat,
}

@Serializable
data class TipOption(
    /** It should be an Int but it is a String for some reason */
    val amount: String,
    val amountType: AmountType,
    val tipsType: TipsType,
)

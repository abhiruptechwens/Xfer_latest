package com.ledgergreen.terminal.data.network.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CustomerRegistrationState {
    @SerialName("exist")
    Exist,

    @SerialName("new")
    New
}

enum class CustomerIdType {
    @SerialName("DRIVING_LICENSE")
    DriverLicense,

    @SerialName("PASSPORT")
    Document,
}

@Serializable
data class KycRequest(
    val idNumber: String,
    val idType: CustomerIdType,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val birthdate: String,
    val expirationDate: String,
    val gender: String,
    val address1: String?,
    val city: String?,
    val state: String?,
    val country: String,
    val issueDate: String?,
    val postalCode: String?,
)

@Serializable
data class RegisterCustomerRequest(
    val idNumber: String,
    val idType: CustomerIdType,
    val birthdate: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val expirationDate: String,
    val issueDate: String?,
    val gender: String,
    val city: String,
    val state: String,
    val country: String,
    val address1: String,
    val postalCode: String,
    val countryCode: Int,
    val phone: String?,
)

@Serializable
data class KycResponse(
    val customer: CustomerResponse?,
    @SerialName("registration_state")
    val registrationState: CustomerRegistrationState
)


    @Serializable
    data class CustomerResponse(
        val id: Long,
        @SerialName("customer_id_number")
        val customerIdNumber: String,
        @SerialName("customer_external_id")
        val customerExternalId: String,
        val fullName: String,
//        @SerialName("is_verified")
//        val isVerified: Boolean,
        @SerialName("customer_type")
        val customerType: CustomerRegistrationState,
        val address1: String?,
        val countryCode: String?,
        val phone: String?,
        val birthdate: String,
        val city: String?,
        val state: String?,
        val country: String,
        val expirationDate: String,
        val gender: String,
        val postalCode: String?,
        @SerialName("is_ban")
        val isBan: Boolean?,
        val firstName: String?,
        val issueDate: String?,
        val lastName: String?,
        val middleName: String?,
        val idType: CustomerIdType,
    )
@Serializable
data class WalletDetails(
    val balance: Double?,
    val createdAt: String?,
    val customer_id: String?,
    val id: Int?,
    val total_loaded: Double?,
    val total_paid: Double?,
    val total_received: Double?,
    val total_transfer: Double?,
    val updatedAt: String?
)

package com.ledgergreen.terminal.ui.common.failure

import androidx.annotation.StringRes
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.network.exception.RequestException
import com.ledgergreen.terminal.domain.exception.ValidationException
import com.ledgergreen.terminal.ui.common.stringresources.StringResources
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import java.io.IOException

fun Throwable.displayableErrorMessage(
    stringResources: StringResources,
    @StringRes defaultMessageResId: Int? = null,
): String {
    val default = stringResources.getString(defaultMessageResId ?: R.string.error_unknown)

    return when (this) {
        is IOException -> stringResources.getString(R.string.error_no_internet_connection)
        is RequestException -> this.message ?: stringResources.getString(R.string.error_request)
        is ValidationException -> this.message
            ?: stringResources.getString(R.string.validation_error)

        is NoTransformationFoundException,
        is ResponseException, // any 3xx, 4xx, 5xx
        is JsonConvertException,
        -> stringResources.getString(R.string.error_server_contact_support)

        else -> default
    }
}

fun Throwable.isNetworkException(): Boolean =
    when {
        this is IOException -> true
        this is RequestException && this.message == "No internet connection" -> true
        else -> false
    }

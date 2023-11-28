package com.ledgergreen.terminal.data.local

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.ledgergreen.terminal.data.network.model.LoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by dataStore(
    "authentication",
    serializer = createSerializer<LoginResponse?>(),
    corruptionHandler = ReplaceFileCorruptionHandler { null },
)

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveLoginResponse(loginResponse: LoginResponse) =
        context.authDataStore.updateData { loginResponse }

    fun getLoginResponse(): Flow<LoginResponse?> = context.authDataStore.data

    suspend fun removeLogin() {
        context.authDataStore.updateData { null }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private inline fun <reified T> createSerializer() = object : Serializer<T?> {
    override val defaultValue: T? = null

    override suspend fun readFrom(input: InputStream): T? = runCatching {
        Json.decodeFromStream<T?>(input)
    }.fold(
        onSuccess = { it },
        onFailure = { null },
    )

    override suspend fun writeTo(t: T?, output: OutputStream) {
        withContext(Dispatchers.IO) {
            if (t != null) {
                Json.encodeToStream(t, output)
            } else {
                ObjectOutputStream(output).writeObject(null)
            }
        }
    }
}

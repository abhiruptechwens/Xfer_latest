package com.ledgergreen.terminal.domain

import com.ledgergreen.terminal.pos.PosAccessory
import javax.inject.Inject
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class InitializePosAccessoryUseCase @Inject constructor(
    private val posAccessory: PosAccessory,
) {
    suspend operator fun invoke() = withContext(Dispatchers.Default) {
        val initTime = measureTimeMillis {
            posAccessory.initialize()
        }
        Timber.d("PosAccessory init time $initTime ms")
    }
}

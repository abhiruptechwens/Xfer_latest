package com.ledgergreen.terminal.idle

import android.os.CountDownTimer
import androidx.compose.runtime.compositionLocalOf
import com.ledgergreen.terminal.idle.IdleLocker.Companion.countDownIntervalMs
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

val LocalIdleLocker = compositionLocalOf<IdleLocker?> { null }

interface IdleLocker {

    val autolockCountDownMs: StateFlow<Long?>

    fun activate(timeout: Long = defaultIdleTimeMs): Flow<Boolean>

    /** Call whenever any user activity detected */
    fun onActivityDetected()

    companion object {
//        const val defaultIdleTimeMs = 70_000L
        const val defaultIdleTimeMs = 10000000L
        const val autolockDialogDurationMs = 10_000L
        const val countDownIntervalMs = 1_000L
    }
}

@Singleton
class CoroutineIdleLocker @Inject constructor() : IdleLocker {

    override val autolockCountDownMs: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val _touchEventStream = MutableStateFlow(0L)

    override fun activate(
        timeout: Long,
    ): Flow<Boolean> = callbackFlow {
        val timer = object : CountDownTimer(timeout, countDownIntervalMs) {
            override fun onTick(millisUntilFinished: Long) {
                autolockCountDownMs.value = millisUntilFinished
            }

            override fun onFinish() {
                autolockCountDownMs.value = null
                trySend(true)
                channel.close()
            }
        }.start()

        invokeOnClose {
            autolockCountDownMs.value = null
            timer.cancel()
        }

        _touchEventStream.collect {
            timer.cancel()
            timer.start()
        }
    }

    override fun onActivityDetected() {
        _touchEventStream.value = Random().nextLong()
    }
}

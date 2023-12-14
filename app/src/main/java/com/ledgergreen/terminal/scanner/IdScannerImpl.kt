package com.ledgergreen.terminal.scanner

import com.ledgergreen.terminal.pos.ScannerException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

class IdScannerImpl @Inject constructor(
    private val scanner: Scanner,
    private val parsers: Set<@JvmSuppressWildcards DocumentParser>,
) : IdScanner {

    override val scannerMethod: ScannerMethod = scanner.scannerMethod

    override suspend fun scan(): Document? = suspendCancellableCoroutine { continuation ->
        val scannerCallback = object : ScannerCallback {
            override fun onResult(result: String) {
                try {
                    parsers.find { parser ->
                        parser.isApplicable(result)
                    }
                        ?.parse(result)
                        ?.onSuccess { document ->
                            continuation.resumeIfActive(document)
                        }
                        ?.onFailure { exception ->
                            Timber.w(exception, "Unable to parse Document")
                            continuation.resumeWithExceptionIfActive(exception)
                        }
                        ?: continuation.resumeWithExceptionIfActive(ScannerException("Unsupported document"))
                } catch (e: Exception) {
                    Timber.e(e, "Error in onResult")
                    continuation.resumeWithExceptionIfActive(e)
                }
            }

            override fun onError(error: String) {
                try {
                    continuation.resumeWithExceptionIfActive(ScannerException(error))
                } catch (e: Exception) {
                    Timber.e(e, "Error in onError")
                }
            }

            override fun onCancel() {
                try {
                    continuation.resumeIfActive(null)
                } catch (e: Exception) {
                    Timber.e(e, "Error in onCancel")
                }
            }
        }

        scanner.start(scannerCallback)

        continuation.invokeOnCancellation {
            scanner.stop()
        }

        scanner.start(scannerCallback)
    }



    // There's a bug in MF SDK. The more you call startScan, the more times you'll receive callback
    // this is a workaround over multiple callback calls from MFSDK

    private fun <T> CancellableContinuation<T>.resumeIfActive(t: T) {
        this.takeIf { it.isActive }?.resume(t)
    }

    private fun <T> CancellableContinuation<T>.resumeWithExceptionIfActive(
        exception: Throwable,
    ) = takeIf { it.isActive }?.resumeWithException(exception)
}

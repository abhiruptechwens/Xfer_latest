package com.ledgergreen.terminal.monitoring

import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumActionType
import javax.inject.Inject
import javax.inject.Singleton

interface Analytics {
    fun trackCustomAction(name: String, attributes: Map<String, Any?> = emptyMap())
    fun trackTap(name: String, attributes: Map<String, Any?> = emptyMap())
}

@Singleton
class DatadogAnalytics @Inject constructor() : Analytics {

    private val globalRum = GlobalRum.get()

    override fun trackCustomAction(name: String, attributes: Map<String, Any?>) {
        globalRum.addUserAction(RumActionType.CUSTOM, name, attributes)
    }

    override fun trackTap(name: String, attributes: Map<String, Any?>) {
        globalRum.addUserAction(RumActionType.TAP, name, attributes)
    }
}

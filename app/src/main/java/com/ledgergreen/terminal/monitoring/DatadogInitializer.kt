package com.ledgergreen.terminal.monitoring

import android.content.Context
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.datadog.android.tracing.AndroidTracer
import com.ledgergreen.terminal.BuildConfig
import io.opentracing.util.GlobalTracer

object DatadogInitializer {

    fun initialize(context: Context) {
        val enabled = !BuildConfig.DEBUG
        val configuration = Configuration.Builder(
            rumEnabled = enabled,
            logsEnabled = enabled,
            crashReportsEnabled = enabled,
            tracesEnabled = enabled,
        )
            .trackInteractions()
            .setUseDeveloperModeWhenDebuggable(true)
            .useSite(DatadogSite.US5)
            .build()

        val credentials = Credentials(
            clientToken = BuildConfig.datadogToken,
            envName = "prod",
            variant = BuildConfig.FLAVOR,
            rumApplicationId = BuildConfig.datadogApplicationId,
        )
        Datadog.initialize(context, credentials, configuration, TrackingConsent.GRANTED)

        val monitor = RumMonitor.Builder().build()
        GlobalRum.registerIfAbsent(monitor)

        val tracer = AndroidTracer.Builder()
            .build()
        GlobalTracer.registerIfAbsent(tracer)
    }
}

package com.ledgergreen.terminal.app

import android.app.Application
import com.datadog.android.log.Logger
import com.datadog.android.timber.DatadogTree
import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.monitoring.DatadogInitializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TerminalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DatadogInitializer.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(
                DatadogTree(
                    Logger.Builder()
                        .setNetworkInfoEnabled(true)
                        .setLogcatLogsEnabled(true)
                        .setDatadogLogsEnabled(true)
                        .build().apply {
                            this.addTag("version_name", BuildConfig.VERSION_NAME)
                            this.addTag("version_code", BuildConfig.VERSION_CODE.toString())
                        }
                )
            )
        }
    }
}

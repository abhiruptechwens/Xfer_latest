package com.ledgergreen.terminal.app

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings.Global.putInt
import android.widget.Toast
import androidx.core.content.edit
import com.datadog.android.log.Logger
import com.datadog.android.timber.DatadogTree
import com.ledgergreen.terminal.BuildConfig
import com.ledgergreen.terminal.monitoring.DatadogInitializer
import dagger.hilt.android.HiltAndroidApp
import java.io.IOException
import timber.log.Timber

@HiltAndroidApp
class TerminalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        clearCacheOnUpdate()
        DatadogInitializer.initialize(this)

//        Sentry.init()
//        Sentry.init("https://2b66406a6701f46993bcf6c105748028@o4506336403783680.ingest.sentry.io/4506337850621952")

//        NewRelic.withApplicationToken("AA0b9ba941d26c76d64bb2a52b82ac7014d38b5c10-NRMA").start(this);

//        SentryAndroid.init(this) { options ->
//            options.dsn = "https://2b66406a6701f46993bcf6c105748028@o4506336403783680.ingest.sentry.io/4506337850621952"
//            // Add a callback that will be used before the event is sent to Sentry.
//            // With this callback, you can modify the event or, when returning null, also discard the event.
//            options.beforeSend =
//                SentryOptions.BeforeSendCallback { event: SentryEvent, hint: Hint ->
//                    if (SentryLevel.DEBUG == event.level) {
//                        null
//                    } else {
//                        event
//                    }
//                }
//        }


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

    private fun clearCacheOnUpdate() {
        val versionCode = BuildConfig.VERSION_CODE
        val lastVersionCode = getSavedVersionCode()



        if (versionCode > lastVersionCode) {
            // App is updated, clear the cache
            clearCache()
            saveVersionCode(versionCode)
            AppState1.lastVersionCode = lastVersionCode
//            showChangelogDialog()
        }
    }

    private fun clearCache() {
        try {
            // Delete cache directory recursively
            applicationContext.cacheDir.deleteRecursively()
            applicationContext.dataDir.deleteRecursively()



//            Toast.makeText(this,"cache and data cleared",Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // Handle exceptions, e.g., if there are issues with file deletion
            e.printStackTrace()
        }
    }

    private fun getSavedVersionCode(): Int {
        // Retrieve the last version code from SharedPreferences or any other storage
        // Return 0 if no version code is saved
        // Example:
        return applicationContext.getSharedPreferences("VERSIONING",MODE_PRIVATE).getInt("VERSION_CODE",0)
    }

    private fun saveVersionCode(versionCode: Int) {
        // Save the current version code to SharedPreferences or any other storage
        // Example:
        applicationContext.getSharedPreferences("VERSIONING", MODE_PRIVATE).edit {
            putInt("VERSION_CODE", versionCode)
        }
    }
}

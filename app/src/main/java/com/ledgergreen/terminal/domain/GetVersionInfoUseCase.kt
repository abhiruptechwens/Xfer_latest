package com.ledgergreen.terminal.domain

import android.content.Context
import com.ledgergreen.terminal.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetVersionInfoUseCase @Inject constructor(@ApplicationContext private val context: Context) {
    @SuppressWarnings("Deprecation")
    operator fun invoke(): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return context.getString(
            R.string.version_code,
            packageInfo.versionName,
            packageInfo.versionCode,
        )
    }
}

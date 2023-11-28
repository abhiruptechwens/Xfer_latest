package com.ledgergreen.terminal.ui.common.stringresources

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface StringResources {
    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String
}

class StringResourcesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : StringResources {
    override fun getString(id: Int) = context.getString(id)
    override fun getString(id: Int, vararg formatArgs: Any?) = context.getString(id, *formatArgs)
}

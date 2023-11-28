package com.ledgergreen.terminal.sfx

import android.content.Context
import android.media.MediaPlayer
import com.ledgergreen.terminal.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SfxEffect @Inject constructor(@ApplicationContext private val context: Context) {

    private var cashSound: MediaPlayer? = null

    fun playTransactionSuccess() {
        if (cashSound == null) {
            cashSound = MediaPlayer.create(context, R.raw.cash_register)
        }
        cashSound!!.start()
    }
}

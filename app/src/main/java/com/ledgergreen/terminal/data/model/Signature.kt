package com.ledgergreen.terminal.data.model

import android.graphics.Bitmap

@JvmInline
value class Signature(val bitmap: Bitmap) {
    fun recycle() = bitmap.recycle()
}

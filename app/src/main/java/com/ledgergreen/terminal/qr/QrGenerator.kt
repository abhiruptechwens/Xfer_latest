package com.ledgergreen.terminal.qr

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import javax.inject.Inject

@JvmInline
value class QrCode(
    val bitmap: Bitmap
)

class QrGenerator @Inject constructor() {

    private val black = 0xFF000000.toInt()
    private val white = 0xFFFFFFFF.toInt()

    fun generateForUrl(url: String): QrCode {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            url,
            BarcodeFormat.QR_CODE,
            500, 500,
        )
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    black
                else
                    white
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return QrCode(bitmap)
    }
}

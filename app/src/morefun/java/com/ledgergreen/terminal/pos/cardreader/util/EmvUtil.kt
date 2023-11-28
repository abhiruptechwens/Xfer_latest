package com.ledgergreen.terminal.pos.cardreader.util

import android.os.Bundle
import androidx.core.os.bundleOf
import com.morefun.yapi.emv.EmvDataSource
import com.morefun.yapi.emv.EmvHandler
import com.morefun.yapi.emv.EmvTermCfgConstrants
import com.morefun.yapi.emv.EmvTransDataConstrants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import timber.log.Timber

object EmvUtil {

    fun emvInitConfig(): Bundle = Bundle().apply {
        putByteArray(
            EmvTermCfgConstrants.TERMCAP,
            byteArrayOf(
                0xE0.toByte(),
                0xE0.toByte(),
                0xC8.toByte(),
            ),
        )
        putByteArray(
            EmvTermCfgConstrants.ADDTERMCAP,
            byteArrayOf(
                0xF2.toByte(),
                0x00,
                0xF0.toByte(),
                0xA0.toByte(),
                0x01,
            ),
        )
        putByteArray(
            EmvTermCfgConstrants.ADD_TERMCAP_EX,
            byteArrayOf(
                0xF2.toByte(),
                0x00,
                0xF0.toByte(),
                0xA0.toByte(),
                0x01,
            ),
        )
        putByte(EmvTermCfgConstrants.TERMTYPE, 0x22)
        putByteArray(EmvTermCfgConstrants.COUNTRYCODE, byteArrayOf(0x03, 0x56))
        putByteArray(EmvTermCfgConstrants.CURRENCYCODE, byteArrayOf(0x03, 0x56))
        putByteArray(
            EmvTermCfgConstrants.TRANS_PROP_9F66,
            byteArrayOf(0x36, 0x00, 0xC0.toByte(), 0x00),
        )
    }

    fun emvTransBundle(): Bundle = bundleOf().apply {
        val date = SimpleDateFormat("yyMMddHHmmss", Locale.US).format(Date())
        putBoolean(EmvTransDataConstrants.EMV_TRANS_ENABLE_CONTACTLESS, true)
        putBoolean(EmvTransDataConstrants.EMV_TRANS_ENABLE_CONTACT, true)
        putInt(EmvTransDataConstrants.CHECK_CARD_TIME_OUT, 30)
        putInt(EmvTransDataConstrants.KERNEL_MODE, 0x00)
        putInt(EmvTransDataConstrants.MKEYIDX, 1)
        putInt(EmvTransDataConstrants.ISQPBOCFORCEONLINE, 1)
        putByte(EmvTransDataConstrants.B9C, 0x00)
        putString(EmvTransDataConstrants.TRANSDATE, date.substring(0, 6))
        putString(EmvTransDataConstrants.TRANSTIME, date.substring(6, 12))
        putString(EmvTransDataConstrants.SEQNO, "00001")

        putString(EmvTransDataConstrants.TRANSAMT, "100")
        putString(EmvTransDataConstrants.MERNAME, "LG")
        putString(EmvTransDataConstrants.MERID, "00000000")
        putString(EmvTransDataConstrants.TERMID, "00000001")
        putBoolean(EmvTransDataConstrants.CONTACT_SERVICE_SWITCH, false)
        putBoolean(EmvTransDataConstrants.BLACK_CARD_HASH_SWITCH, false)
        putStringArrayList(
            EmvTransDataConstrants.TERMINAL_TLVS,
            arrayListOf("DF81180170", "DF81190118"),
        )
    }

    fun getPbocData(emvHandler: EmvHandler, tagName: String, isHex: Boolean): String? {
        val data = ByteArray(512)
        val len = emvHandler.readEmvData(
            arrayOf(tagName.uppercase()),
            data,
            Bundle(),
        )
        if (len > 0) {
            val tlvData =
                TlvData.fromRawData(
                    HexUtil.subByte(data, 0, len),
                    0,
                )
            return if (isHex) {
                tlvData.value
            } else {
                tlvData.gbkValue
            }
        }
        return null
    }

    fun readTrack2(emvHandler: EmvHandler): String? {
        val track2 = getPbocData(emvHandler, EmvDataSource.GET_TRACK2_TAG_6B, true)
        return if (!track2.isNullOrEmpty() && track2.endsWith("F")) {
            track2.substring(0, track2.length - 1)
        } else track2
    }

    fun readPan(emvHandler: EmvHandler): String? {
        val pan = getPbocData(emvHandler, "5A", true)
        if (pan.isNullOrEmpty()) {
            return getPanFromTrack2(emvHandler)
        }
        return if (pan.endsWith("F")) {
            pan.substring(0, pan.length - 1)
        } else pan
    }

    fun readExpiryDate(emvHandler: EmvHandler): String? {
        val rawExpiryDate = getTLVDatas(emvHandler, listOf("5F24"))
        return try {
            // sample: "5F2403260831"
            val yearMonth = rawExpiryDate.drop(6).dropLast(2)
            yearMonth.takeLast(2) + yearMonth.take(2)
        } catch (e: Throwable) {
            Timber.w(e, "unable to get expiry date")
            null
        }
    }


    fun getPanFromTrack2(emvHandler: EmvHandler): String? {
        val track2 = readTrack2(emvHandler)
        if (track2 != null) {
            for (i in track2.indices) {
                if (track2[i] == '=' || track2[i] == 'D') {
                    val endIndex = i.coerceAtMost(19)
                    return track2.substring(0, endIndex)
                }
            }
        }
        return null
    }

    fun getTLVDatas(emvHandler: EmvHandler, tags: List<String>): String {
        val buffer = ByteArray(3096)
        val byteNum = emvHandler.readEmvData(
            tags.map { it.uppercase() }
                .toTypedArray(),
            buffer,
            Bundle(),
        )
        return if (byteNum > 0) {
            HexUtil.bytesToHexString(HexUtil.subByte(buffer, 0, byteNum))
        } else {
            ""
        }
    }

    fun getExampleARPCData(): ByteArray = HexUtil.hexStringToByte("8A023030")
}

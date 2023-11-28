package com.ledgergreen.terminal.pos.card

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.nexgo.oaf.apiv3.emv.AidEntity
import com.nexgo.oaf.apiv3.emv.CapkEntity
import com.nexgo.oaf.apiv3.emv.EmvProcessFlowEnum
import com.nexgo.oaf.apiv3.emv.EmvTransConfigurationEntity
import java.io.BufferedReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmvUtils(
    context: Context,
) {

    private val assets = context.assets

    private var cachedAidList: List<AidEntity>? = null
    private var cachedCapkList: List<CapkEntity>? = null

    fun getAidList(): List<AidEntity> {
        if (cachedAidList == null) {
            val gson = Gson()
            val parser = JsonParser()
            cachedAidList = parser.parse(readAssetsAsTxt("inbas_aid.json")).asJsonArray
                .map {
                    gson.fromJson(it, AidEntity::class.java)
                }
        }
        return cachedAidList!!
    }

    fun getCapkList(): List<CapkEntity> {
        if (cachedCapkList == null) {
            val gson = Gson()
            val parser = JsonParser()
            cachedCapkList = parser.parse(readAssetsAsTxt("inbas_capk.json")).asJsonArray
                .map {
                    gson.fromJson(it, CapkEntity::class.java)
                }
        }
        return cachedCapkList!!
    }

    private fun readAssetsAsTxt(filename: String): String =
        assets
            .open(filename)
            .bufferedReader()
            .use(BufferedReader::readText)

    companion object {

        private val transAmt = leftPad("100", 12, '0') // 50.00 -> 5000, 100 -> 1.00

        fun createEmvTransConf() = EmvTransConfigurationEntity().apply {
            transAmount = transAmt
            termId = "00000001" //Set the terminal id
            merId = "000000000000001" //Set the merchant id
            emvTransType = 0x00 // EMV Transaction Type, sale-0x00, refund-0x20...
            transDate = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
            transTime = SimpleDateFormat("hhmmss", Locale.getDefault()).format(Date())
            traceNo = "00000001"
            emvProcessFlowEnum = EmvProcessFlowEnum.EMV_PROCESS_FLOW_READ_APPDATA
        }

        /**
         * Helper function.
         *
         * Pad the amount in the correct format
         *
         * @param amountStr The amount string. No decimal places should be present, and two digits should
         * be reserved for 'cents' (i.e. $50.00 == 5000)
         * @param size the final size/length of the amount string; function will pad with correct number
         * of digits to meet this parameter.
         * @param padChar the character to 'pad' with. (i.e. '0' will pad the digits with 0's (00000005)
         * @return the correctly formatted/padded transaction amont string.
         */
        private fun leftPad(amountStr: String?, size: Int, padChar: Char): String {
            val padded = StringBuilder(amountStr ?: "")
            while (padded.length < size) {
                padded.insert(0, padChar)
            }
            return padded.toString()
        }
    }
}

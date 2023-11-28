package com.ledgergreen.terminal.pos.cardreader

import android.os.Bundle
import androidx.core.os.bundleOf
import com.ledgergreen.terminal.pos.MfService
import com.ledgergreen.terminal.pos.card.CardDetails
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.card.CardReaderEvent
import com.ledgergreen.terminal.pos.card.CardReaderEventListener
import com.ledgergreen.terminal.pos.card.CardReaderMethod
import com.ledgergreen.terminal.pos.cardreader.util.EmvUtil
import com.morefun.yapi.ServiceResult
import com.morefun.yapi.device.reader.icc.IccCardType
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity
import com.morefun.yapi.device.reader.mag.OnSearchMagCardListener
import com.morefun.yapi.emv.EmvOnlineResult
import com.morefun.yapi.emv.OnEmvProcessListener
import timber.log.Timber

class MfCardReader(
    private val mfService: MfService,
) : CardReader {

    companion object {
        private const val timeoutSec = 70
    }

    private var listener: CardReaderEventListener? = null

    override fun start(listener: CardReaderEventListener) {
        this.listener = listener

        mfService.magCardReader.searchCard(
            object : OnSearchMagCardListener.Stub() {
                override fun onSearchResult(result: Int, magCardEntity: MagCardInfoEntity?) {
                    Timber.d("MAG onSearchResult $result")
                    magCardEntity?.let {
                        val cardDetails = CardDetails(
                            number = it.cardNo,
                            expiry = it.expDate,
                            method = CardReaderMethod.SWIPE,
                        )
                        listener(CardReaderEvent.CardFound(cardDetails))
                    }
                    stop()
                }
            },
            timeoutSec,
            bundleOf(),
        )

        mfService.rfCardReader.searchCard(
            object : OnSearchIccCardListener.Stub() {
                override fun onSearchResult(retCode: Int, bundle: Bundle?) {
                    Timber.d("RF onSearchResult $retCode $bundle")
                    startEmvProcess(isIcc = false, listener)
                }
            },
            timeoutSec,
            arrayOf(IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102),
        )

        mfService.iccCardReader.searchCard(
            object : OnSearchIccCardListener.Stub() {
                override fun onSearchResult(retCode: Int, bundle: Bundle?) {
                    Timber.d("ICC onSearchResult $retCode $bundle")
                    startEmvProcess(isIcc = true, listener)
                }
            },
            timeoutSec,
            arrayOf(IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102),
        )

        listener(CardReaderEvent.Start)
    }

    private fun startEmvProcess(isIcc: Boolean, listener: CardReaderEventListener) {
        listener(CardReaderEvent.StartEmvProcess)

        mfService.emvHandler.initTermConfig(EmvUtil.emvInitConfig())
        mfService.emvHandler.emvTrans(
            EmvUtil.emvTransBundle(),
            object : OnEmvProcessListener.Stub() {
                override fun onSelApp(appNameList: MutableList<String>?, isFirstSelect: Boolean) {
                    Timber.d("onSelApp $appNameList")
                    mfService.emvHandler.onSetSelAppResponse(1)
                }

                override fun onConfirmCardNo(cardNo: String?) {
                    Timber.d("onConfirmCardNo")
                    mfService.emvHandler.onSetConfirmCardNoResponse(true)
                }

                override fun onCardHolderInputPin(isOnlinePin: Boolean, leftTimes: Int) {
                    Timber.d("onCardHolderInputPin $isOnlinePin, $leftTimes")
                    mfService.emvHandler.onSetCardHolderInputPin(byteArrayOf(0x01, 0x02, 0x03))
                }

                override fun onPinPress(keyCode: Byte) {
                    Timber.d("onPinPress $keyCode")
                }

                override fun onCertVerify(certName: String?, certInfo: String?) {
                    Timber.d("onCertVerify $certName, $certInfo")
                    mfService.emvHandler.onSetCertVerifyResponse(true)
                }

                override fun onOnlineProc(bundle: Bundle?) {
                    Timber.d("onOnlineProc")
                    onlineProc()
                }

                override fun onContactlessOnlinePlaceCardMode(mode: Int) {
                    Timber.d("onContactlessOnlinePlaceCardMode $mode")
                }

                override fun onFinish(resultCode: Int, data: Bundle?) {
                    Timber.d("onFinish $resultCode $data")

                    val pan = EmvUtil.readPan(mfService.emvHandler)
                    val expiryDate = EmvUtil.readExpiryDate(mfService.emvHandler)

                    if (pan != null && expiryDate != null) {
                        listener(
                            CardReaderEvent.CardFound(
                                CardDetails(
                                    number = pan,
                                    expiry = expiryDate,
                                    method = if (isIcc) CardReaderMethod.ICC
                                    else CardReaderMethod.RF,
                                ),
                            ),
                        )
                    } else {
                        listener(
                            CardReaderEvent.ErrorMessage(
                                message = "Unable to read PAN or Expiry date",
                                stopped = true,
                            ),
                        )
                    }

                    stop()
                }

                override fun onSetAIDParameter(aid: String?) {
                    Timber.d("onSetAIDParameter $aid")
                }

                override fun onSetCAPubkey(rid: String?, index: Int, algMode: Int) {
                    Timber.d("onSetCAPubkey $rid, $index, $algMode")
                }

                override fun onTRiskManage(pan: String?, panSn: String?) {
                    Timber.d("onTRiskManage")
                }

                override fun onSelectLanguage(language: String?) {
                    Timber.d("onSelectLanguage $language")
                }

                override fun onSelectAccountType(accountTypes: MutableList<String>?) {
                    Timber.d("onSelectAccountType $accountTypes")
                }

                override fun onIssuerVoiceReference(pan: String?) {
                    Timber.d("onIssuerVoiceReference")
                }

                override fun onDisplayOfflinePin(retCode: Int) {
                    Timber.d("onDisplayOfflinePin $retCode")
                }

                override fun inputAmount(type: Int) {
                    Timber.d("inputAmount $type")
                    mfService.emvHandler.onSetInputAmountResponse("0.03")
                }

                override fun onGetCardResult(retCode: Int, bundle: Bundle?) {
                    Timber.d("onGetCardResult $retCode")
                }

                override fun onDisplayMessage() {
                    Timber.d("onDisplayMessage")
                }

                override fun onUpdateServiceAmount(serviceRelatedData: String?) {
                    Timber.d("onUpdateServiceAmount $serviceRelatedData")
                }

                override fun onCheckServiceBlackList(pan: String?, amount: String?) {
                    Timber.d("onCheckServiceBlackList")
                }

                override fun onGetServiceDirectory(directory: ByteArray?) {
                    Timber.d("onGetServiceDirectory $directory")
                }

                override fun onRupayCallback(type: Int, bundle: Bundle?) {
                    Timber.d("onRupayCallback $type")
                }
            },
        )
    }

    override fun stop() {
        mfService.emvHandler.endPBOC()
        mfService.iccCardReader.stopSearch()
        mfService.rfCardReader.stopSearch()
        listener?.invoke(CardReaderEvent.Stop)
    }

    private fun onlineProc() {
        val arpcData: ByteArray = EmvUtil.getExampleARPCData()

        val bundle = Bundle()
        bundle.putString(EmvOnlineResult.REJCODE, "00")
        bundle.putByteArray(EmvOnlineResult.RECVARPC_DATA, arpcData)

        mfService.emvHandler.onSetOnlineProcResponse(ServiceResult.Success, bundle)
    }
}

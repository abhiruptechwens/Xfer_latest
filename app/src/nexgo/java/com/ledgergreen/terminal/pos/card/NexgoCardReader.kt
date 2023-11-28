package com.ledgergreen.terminal.pos.card

import com.nexgo.oaf.apiv3.device.reader.CardReader as InnerCardReader
import com.ledgergreen.terminal.pos.NexgoEngineProvider
import com.nexgo.common.ByteUtils
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.device.reader.OnCardInfoListener
import com.nexgo.oaf.apiv3.emv.CandidateAppInfoEntity
import com.nexgo.oaf.apiv3.emv.EmvEntryModeEnum
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import com.nexgo.oaf.apiv3.emv.EmvOnlineResultEntity
import com.nexgo.oaf.apiv3.emv.EmvProcessResultEntity
import com.nexgo.oaf.apiv3.emv.OnEmvProcessListener2
import com.nexgo.oaf.apiv3.emv.PromptEnum
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NexgoCardReader @Inject constructor(
    private val deviceEngineProvider: NexgoEngineProvider,
) : CardReader {

    private lateinit var emvHandler: EmvHandler2
    private lateinit var cardReader: InnerCardReader

    private val cardTypes = HashSet<CardSlotTypeEnum?>().apply {
        add(CardSlotTypeEnum.ICC1)
        add(CardSlotTypeEnum.RF)
        add(CardSlotTypeEnum.SWIPE)
    }

    private val timeoutSec = 70

    private var listener: CardReaderEventListener? = null

    override fun start(listener: CardReaderEventListener) {
        Timber.i("Start scanner")
        this.listener = listener

        emvHandler = deviceEngineProvider.deviceEngine.getEmvHandler2("app1").apply {
            deviceEngineProvider.initEmvHandler(this)
        }
        cardReader = deviceEngineProvider.deviceEngine.cardReader

        Timber.v("Search card")
        cardReader.searchCard(
            cardTypes,
            timeoutSec,
            onCardInfoListener,
        )
    }

    private fun notifyListeners(event: CardReaderEvent) {
        listener?.invoke(event)
    }

    override fun stop() {
        Timber.d("Stop card reader")
        emvHandler.emvProcessCancel()
        cardReader.stopSearch()
    }

    private val onCardInfoListener = object : OnCardInfoListener {
        override fun onCardInfo(retCode: Int, cardInfo: CardInfoEntity?) {
            Timber.v("onCardInfo $retCode")
            if (retCode == SdkResult.Success && cardInfo != null) {
                val emvTransConf = EmvUtils.createEmvTransConf()
                Timber.v("Card slot: ${cardInfo.cardExistslot}")
                when (cardInfo.cardExistslot) {
                    CardSlotTypeEnum.RF ->
                        emvTransConf.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACTLESS

                    CardSlotTypeEnum.ICC1 ->
                        emvTransConf.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACT

                    CardSlotTypeEnum.SWIPE -> {
                        // swipe method doesn't require emv processing
                        val cardData = cardInfo.toCardData()
                        if (cardData != null) {
                            Timber.v("Card found using SWIPE")
                            notifyListeners(CardReaderEvent.CardFound(cardData))
                            cardReader.stopSearch()
                        } else {
                            // something went wrong. restart
                            Timber.v("Unable to read card by SWIPE")
                            // actually it is not a part of emv process. but this is an error
                            notifyListeners(
                                CardReaderEvent.ErrorMessage(
                                    message = "Swipe incorrect",
                                    stopped = true,
                                ),
                            )
                            cardReader.stopSearch()
                        }
                        return
                    }

                    else -> {
                        Timber.wtf("Unexpected card slot ${cardInfo.cardExistslot}. Exit")
                        error("Unexpected card slot ${cardInfo.cardExistslot}")
                    }
                }
                emvHandler.setTlv(
                    ByteUtils.hexString2ByteArray("9F33"),
                    ByteUtils.hexString2ByteArray("E0F8C8"),
                )
                // currency code
                emvHandler.initTermConfig(ByteUtils.hexString2ByteArray("9f1a0208405f2a0208409f3c020840"))
                // Make the call to begin the actual EMV process
                Timber.v("start emvProcess")
                notifyListeners(CardReaderEvent.StartEmvProcess)
                emvHandler.emvProcess(emvTransConf, onEmvProcessListener)
            } else {
                Timber.v("Insert card")
                notifyListeners(CardReaderEvent.ErrorMessage("Insert card", stopped = true))
            }
        }

        override fun onSwipeIncorrect() {
            Timber.w("onSwipeIncorrect")
            // this callback does not stop card reader
            notifyListeners(CardReaderEvent.ErrorMessage("Swipe incorrect", stopped = false))
        }

        override fun onMultipleCards() {
            Timber.w("onMultipleCards")
            // this callback does not stop card reader
            notifyListeners(CardReaderEvent.ErrorMessage("Multiple cards", stopped = false))
        }
    }

    private val onEmvProcessListener = object : OnEmvProcessListener2 {
        override fun onSelApp(
            appNameList: MutableList<String>?,
            appInfoList: MutableList<CandidateAppInfoEntity>?,
            isFirstSelect: Boolean,
        ) {
            Timber.v("onSelApp $appNameList $isFirstSelect")
            emvHandler.onSetSelAppResponse(1)
        }

        override fun onTransInitBeforeGPO() {
            Timber.v("onTransInitBeforeGP0")
            emvHandler.onSetContactlessTapCardResponse(true)
        }

        override fun onConfirmCardNo(cardInfo: CardInfoEntity?) {
            Timber.v("onConfirmCardNo")
            emvHandler.onSetConfirmCardNoResponse(true)
        }

        override fun onCardHolderInputPin(isOnlinePin: Boolean, leftTimes: Int) {
            Timber.v("onCardHolderInputPin isOnline? $isOnlinePin leftTimes: $leftTimes")
        }

        override fun onContactlessTapCardAgain() {
            Timber.v("onContactlessTapCardAgain")
            notifyListeners(CardReaderEvent.ErrorMessage("Tap card again", stopped = false))
        }

        override fun onOnlineProc() {
            Timber.v("onOnlineProc")
            val onlineResult = EmvOnlineResultEntity().apply {
                rejCode = "00"
                recvField55 = null
            }
            emvHandler.onSetOnlineProcResponse(SdkResult.Success, onlineResult)
        }

        override fun onPrompt(prompt: PromptEnum?) {
            Timber.v("onPrompt $prompt")
        }

        override fun onRemoveCard() {
            Timber.v("onRemoveCard")
        }

        override fun onFinish(retCode: Int, emvProcessResultEntity: EmvProcessResultEntity?) {
            val retCodeMessage = when (retCode) {
                SdkResult.Success -> "Success"
                SdkResult.Fail -> "Fail"
                SdkResult.Emv_USE_OTHER_CARD -> "Emv_USE_OTHER_CARD: Use other card"
                SdkResult.Emv_FallBack -> "Emv_Fallback: Transaction fallback, need to swipe card"
                SdkResult.Emv_Communicate_Timeout -> "Emv_Communicate_Timeout: Transaction Communicate Timeout"
                SdkResult.Emv_Command_Fail -> "Emv_Command_Fail: Command fail"
                SdkResult.Emv_Cancel -> "Emv_Cancel: Cancel the transaction"
                else -> "Unknown $retCode"
            }

            val cardData = emvHandler.emvCardDataInfo.toCardData()
            if (cardData != null) {
                Timber.v("CardReader success")
                notifyListeners(CardReaderEvent.CardFound(cardData))
            } else {
                Timber.w("CardReader failed with errorCode $retCode $retCodeMessage")
                notifyListeners(CardReaderEvent.ErrorMessage(retCodeMessage, true))
            }
        }
    }

    private fun CardInfoEntity.toCardData(): CardDetails? = cardNo?.let {
        // expiredDate goes as YYMM. swap it
        val exp = expiredDate.drop(2) + expiredDate.take(2)

        val readMethod = when (cardExistslot) {
            CardSlotTypeEnum.ICC1, CardSlotTypeEnum.ICC2, CardSlotTypeEnum.ICC3 -> CardReaderMethod.ICC
            CardSlotTypeEnum.RF -> CardReaderMethod.RF
            CardSlotTypeEnum.SWIPE -> CardReaderMethod.SWIPE
            else -> error("cardExistslot is null")
        }

        CardDetails(
            number = cardNo,
            expiry = exp,
            method = readMethod,
        )
    }
}

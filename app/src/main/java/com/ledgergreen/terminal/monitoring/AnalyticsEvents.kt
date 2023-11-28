package com.ledgergreen.terminal.monitoring


// https://us5.datadoghq.com/dashboard/9zn-24v-4br/terminal-actions-dashboard
object Actions {
    const val documentScan = "document_scanned"
    const val cardScanned = "card_scanned"
    const val scanTransactionSuccess = "scan_transaction_success"
    const val contactlessPaymentCreated = "contactless_payment_created"
    const val tipsSelected = "tips_selected"
    const val failedTransaction = "failed_transaction"
    const val clearPin = "clear_pin"
    const val phoneNumberAttached = "phone_number_attached"
    const val idleLockInterrupted = "idle_lock_interrupted"
}

object Clicks {
    const val enterCardManually = "enter_card_manually"
    const val savedCardClicked = "saved_card_clicked"
    const val rescanCard = "rescan_card"
    const val scanDriverLicense = "scan_driver_license"
    const val rescanDriverLicense = "rescan_driver_license"
    const val contactless = "contactless"
    const val addNewButton = "add_new_button"
    const val sendContactlessViaQR = "contactless_via_qr"
    const val sendContactlessViaSMS = "contactless_via_sms"
    const val transactionListFilter = "transaction_list_filter"
}

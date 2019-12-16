package br.com.nubank2ynab.core

import java.time.LocalDateTime
import java.time.ZoneOffset

class CreateYNABTransactionFromNuBankNotification(
        private val YNABGateway: YNABGateway,
        private val payeeToCategoryGateway: PayeeToCategoryGateway
) {

    private var lastDuplicationTimeout: LocalDateTime = LocalDateTime.now()
    private var duplicationControl: MutableSet<String> = mutableSetOf()

    fun create(packageName: String, notificationText: String) {
        val isNotNubankNotification = packageName != "com.nu.production"
        if (isNotNubankNotification) return
        if (isDuplicateNotification(notificationText)) return

        val payee = extractPayee(notificationText)
        this.YNABGateway.create(YNABTransaction(
                payee,
                payee,
                extractAmount(notificationText),
                LocalDateTime.now(), payeeToCategoryGateway.get(payee)))
    }

    private fun isDuplicateNotification(notificationText: String): Boolean {
        val now = LocalDateTime.now()
        val timeSinceDuplicationControlIsNotReset = now.toEpochSecond(ZoneOffset.UTC) - lastDuplicationTimeout.toEpochSecond(ZoneOffset.UTC)
        if (timeSinceDuplicationControlIsNotReset > 10) {
            duplicationControl.clear()
            lastDuplicationTimeout = now
        }

        if (duplicationControl.contains(notificationText)) {
            return true
        } else {
            duplicationControl.clear()
            duplicationControl.add(notificationText)
        }
        return false
    }


    private fun extractAmount(text: String): Int {
        val amountAsText = "R\\$ (.*) APROVADA".toRegex().find(text)?.groups?.get(1)?.value ?: "0"
        val amountWithoutFormatting = amountAsText.replace("[.,]".toRegex(), "")
        val amoutWithExtraZeroForYNAB = amountWithoutFormatting + "0"
        return -Integer.parseInt(amoutWithExtraZeroForYNAB)
    }

    private fun extractPayee(text: String): String {
        return "APROVADA em (.*)\\.".toRegex().find(text)?.groups?.get(1)?.value ?: ""
    }

}
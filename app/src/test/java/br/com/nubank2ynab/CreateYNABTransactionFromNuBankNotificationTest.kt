package br.com.nubank2ynab

import br.com.nubank2ynab.core.CreateYNABTransactionFromNuBankNotification
import br.com.nubank2ynab.core.PayeeToCategoryGateway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateYNABTransactionFromNuBankNotificationTest {

    private val payeeToCategoryGateway: PayeeToCategoryGateway = PayeeToCategoryGatewayFake()
    private var YNABGateway: YNABGatewayFake = YNABGatewayFake()
    private var usecase: CreateYNABTransactionFromNuBankNotification

    init {
        this.usecase = CreateYNABTransactionFromNuBankNotification(this.YNABGateway, this.payeeToCategoryGateway)
    }

    @Test
    fun `Do nothing when package name is not nubank related`() {
        this.usecase.create("not.from.nubank", "bla-bla-bla")
        assertEquals(0, YNABGateway.transactions.count())
    }

    @Test
    fun `Parse text with amount lower than 1000 to YNAB format in cents`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1,59 APROVADA em BLABLA BLA.")
        assertEquals(-1590, YNABGateway.transactions.get(0).amount)
    }

    @Test
    fun `Parse text with amount greater than 1000 to YNAB format in cents`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em BLABLA BLA.")
        assertEquals(-1234560, YNABGateway.transactions.get(0).amount)
    }

    @Test
    fun `Extract payee from the notification text`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        assertEquals("Nice Looking Company", YNABGateway.transactions.get(0).payee)
    }

    @Test
    fun `Extract memo from the notification text (which is the same as the payee)`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        assertEquals("Nice Looking Company", YNABGateway.transactions.get(0).payee)
        assertEquals("Nice Looking Company", YNABGateway.transactions.get(0).memo)
    }

    @Test
    fun `Ignore multiple invocations until something new appears`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        this.usecase.create("com.nu.production", "Compra de R$ 100,00 APROVADA em Bad Looking Company.")
        this.usecase.create("com.nu.production", "Compra de R$ 100,00 APROVADA em Bad Looking Company.")
        assertEquals(2, YNABGateway.transactions.count())
    }

    @Test
    fun `Use the category previously set up for given payee`() {
        this.payeeToCategoryGateway.put("Nice Looking Company", "YNAB_CATEGORY_ID=9999")

        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")

        assertEquals(1, YNABGateway.transactions.count())
        assertEquals("YNAB_CATEGORY_ID=9999", YNABGateway.transactions.get(0).categoryId)
    }

    @Test
    fun `Allow multiple invocations after some time`() {
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        Thread.sleep(11000)
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        this.usecase.create("com.nu.production", "Compra de R$ 1.234,56 APROVADA em Nice Looking Company.")
        assertEquals(2, YNABGateway.transactions.count())
    }
}
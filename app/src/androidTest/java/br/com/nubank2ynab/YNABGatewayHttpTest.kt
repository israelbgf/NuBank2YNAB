package br.com.nubank2ynab

import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.nubank2ynab.android.HardcodedConfig
import br.com.nubank2ynab.android.YNABGatewayHttp
import br.com.nubank2ynab.core.YNABTransaction
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class YNABGatewayHttpTest {
    @Test
    fun gatewayTest() {
        val gateway = YNABGatewayHttp(HardcodedConfig.YNAB_API_TOKEN,
                HardcodedConfig.BUDGET_ID, HardcodedConfig.ACCOUNT_ID)

        gateway.create(YNABTransaction("Test Android App", -999, LocalDateTime.now(),
                categoryId = null))
    }
}

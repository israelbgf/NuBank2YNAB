package com.example.nubank2ynab

import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.nubank2ynab.core.YNABTransaction
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class YNABGatewayHttpTest {
    @Test
    fun gatewayTest() {
        var gateway = YNABGatewayHttp("")
        gateway.create(YNABTransaction("Test", 10, LocalDateTime.now()))
    }
}

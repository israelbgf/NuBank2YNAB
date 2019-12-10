package br.com.nubank2ynab

import br.com.nubank2ynab.core.YNABGateway
import br.com.nubank2ynab.core.YNABTransaction

class YNABGatewayFake : YNABGateway {

    var transactions: MutableList<YNABTransaction> = mutableListOf()

    override fun create(transaction: YNABTransaction) {
        transactions.add(transaction)
    }
}
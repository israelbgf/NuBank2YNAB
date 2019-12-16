package br.com.nubank2ynab

import br.com.nubank2ynab.core.PayeeToCategoryGateway

class PayeeToCategoryGatewayFake : PayeeToCategoryGateway {

    var map = mutableMapOf("a" to "a")

    override fun put(payee: String, categoryId: String) {
        map[payee] = categoryId
    }

    override fun get(payee: String): String? {
        return map.get(payee)
    }
}
package br.com.nubank2ynab.core

interface PayeeToCategoryGateway {
    fun put(payee: String, categoryId: String)
    fun get(payee: String): String?
}
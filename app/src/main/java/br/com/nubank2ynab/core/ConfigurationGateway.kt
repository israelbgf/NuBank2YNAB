package br.com.nubank2ynab.core

interface ConfigurationGateway {
    
    fun put(key: String, value: String)

    fun get(key: String): String?

}

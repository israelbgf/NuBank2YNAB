package br.com.nubank2ynab.core


class ConfigurationGatewayInMemory : ConfigurationGateway {

    private val configurations: HashMap<String, String> = HashMap()

    override fun get(key: String): String? {
        return configurations[key]
    }

    override fun put(key: String, value: String) {
        configurations[key] = value
    }
}
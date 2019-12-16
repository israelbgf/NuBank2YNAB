package br.com.nubank2ynab.android

import br.com.nubank2ynab.core.YNABGateway
import br.com.nubank2ynab.core.YNABTransaction
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class YNABGatewayHttp(val YNABAPIToken: String, val budgetId: String, val accountId: String) : YNABGateway {
    override fun create(transaction: YNABTransaction) {
        val postData = mapOf("transaction" to mapOf(
                "account_id" to accountId,
                "amount" to transaction.amount,
                "category_id" to transaction.categoryId,
                "date" to transaction.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "memo" to transaction.payee))

        val postBody = JSONObject(postData).toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer $YNABAPIToken")
                .method("POST", postBody)
                .url("https://api.youneedabudget.com/v1/budgets/$budgetId/transactions")
                .build()

        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException(response.body?.string())
        }
    }
}
package br.com.nubank2ynab.core

import java.time.LocalDateTime

data class YNABTransaction(
        val payee: String,
        val amount: Int,
        val date: LocalDateTime,
        val categoryId: String?
)
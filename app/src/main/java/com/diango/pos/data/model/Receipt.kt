// data/model/Receipt.kt
package com.diango.pos.data.model

data class Receipt(
    val transactionId: String,
    val merchantName: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val authorizationCode: String?,
    val dateTime: String,
    val customerCopy: Boolean,
    val installments: Int = 1
)
// data/model/PaymentMethod.kt
package com.diango.pos.data.model

enum class PaymentMethod(val value: String, val displayName: String) {
    CREDIT_CARD("credit_card", "Cartão de Crédito"),
    DEBIT_CARD("debit_card", "Cartão de Débito"),
    PIX("pix", "PIX"),
    VOUCHER("voucher", "Voucher"),
    CASH("cash", "Dinheiro")
}
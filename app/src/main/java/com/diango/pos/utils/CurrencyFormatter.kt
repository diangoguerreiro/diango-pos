// utils/CurrencyFormatter.kt
package com.diango.pos.utils

import java.text.NumberFormat
import java.util.*

object CurrencyFormatter {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    fun format(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    fun format(amount: String): String {
        return try {
            format(amount.toDouble())
        } catch (e: NumberFormatException) {
            "R$ 0,00"
        }
    }
    
    fun parseAmount(formattedAmount: String): Double {
        return try {
            formattedAmount
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
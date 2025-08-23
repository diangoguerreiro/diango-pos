// data/model/TransactionRequest.kt
package com.diango.pos.data.model

import com.google.gson.annotations.SerializedName

data class TransactionRequest(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("installments")
    val installments: Int = 1,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("customer")
    val customer: Customer?
)
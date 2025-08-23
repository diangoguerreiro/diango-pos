// data/model/TransactionResponse.kt
package com.diango.pos.data.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("authorization_code")
    val authorizationCode: String?,
    
    @SerializedName("receipt_url")
    val receiptUrl: String?,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("payment_method")
    val paymentMethod: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("error_message")
    val errorMessage: String?
)
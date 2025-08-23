// data/model/Payment.kt
package com.diango.pos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String?,
    val authorizationCode: String?,
    val receipt: String?,
    val customerName: String?,
    val customerDocument: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val description: String?
)
// domain/repository/PaymentRepository.kt
package com.diango.pos.domain.repository

import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.data.model.TransactionRequest
import com.diango.pos.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface PaymentRepository {
    
    // Local operations
    fun getAllPayments(): Flow<List<Payment>>
    suspend fun getPaymentById(id: String): Payment?
    suspend fun insertPayment(payment: Payment)
    suspend fun updatePayment(payment: Payment)
    suspend fun deletePayment(payment: Payment)
    fun getPaymentsByStatus(status: PaymentStatus): Flow<List<Payment>>
    fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>>
    fun getPaymentsByDateRange(startDate: Date, endDate: Date): Flow<List<Payment>>
    suspend fun getTodaysTotalSales(): Double
    suspend fun getTodaysTransactionCount(): Int
    
    // Remote operations
    suspend fun createTransaction(request: TransactionRequest): NetworkResult<Payment>
    suspend fun getTransactionStatus(transactionId: String): NetworkResult<Payment>
    suspend fun cancelTransaction(transactionId: String): NetworkResult<Payment>
    suspend fun syncPaymentsWithServer(): NetworkResult<List<Payment>>
}
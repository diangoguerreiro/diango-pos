// data/local/PaymentDao.kt
package com.diango.pos.data.local

import androidx.room.*
import androidx.lifecycle.LiveData
import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.data.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PaymentDao {
    
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getPaymentById(id: String): Payment?
    
    @Query("SELECT * FROM payments WHERE status = :status ORDER BY createdAt DESC")
    fun getPaymentsByStatus(status: PaymentStatus): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE paymentMethod = :method ORDER BY createdAt DESC")
    fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE DATE(createdAt) = DATE(:date) ORDER BY createdAt DESC")
    fun getPaymentsByDate(date: Date): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getPaymentsByDateRange(startDate: Date, endDate: Date): Flow<List<Payment>>
    
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'APPROVED' AND DATE(createdAt) = DATE('now')")
    suspend fun getTodaysTotalSales(): Double?
    
    @Query("SELECT COUNT(*) FROM payments WHERE DATE(createdAt) = DATE('now')")
    suspend fun getTodaysTransactionCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>)
    
    @Update
    suspend fun updatePayment(payment: Payment)
    
    @Delete
    suspend fun deletePayment(payment: Payment)
    
    @Query("DELETE FROM payments WHERE createdAt < :date")
    suspend fun deleteOldPayments(date: Date)
    
    @Query("DELETE FROM payments")
    suspend fun deleteAllPayments()
}
// data/repository/PaymentRepositoryImpl.kt
package com.diango.pos.data.repository

import com.diango.pos.data.local.PaymentDao
import com.diango.pos.data.model.*
import com.diango.pos.data.remote.PagBankApiService
import com.diango.pos.data.remote.BackendApiService
import com.diango.pos.domain.repository.PaymentRepository
import com.diango.pos.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val localDataSource: PaymentDao,
    private val pagBankApiService: PagBankApiService,
    private val backendApiService: BackendApiService
) : PaymentRepository {
    
    override fun getAllPayments(): Flow<List<Payment>> {
        return localDataSource.getAllPayments()
    }
    
    override suspend fun getPaymentById(id: String): Payment? {
        return localDataSource.getPaymentById(id)
    }
    
    override suspend fun insertPayment(payment: Payment) {
        localDataSource.insertPayment(payment)
        
        // Tentar sincronizar com o backend
        try {
            backendApiService.savePayment("", payment)
        } catch (e: Exception) {
            // Log error but don't fail the local insertion
            e.printStackTrace()
        }
    }
    
    override suspend fun updatePayment(payment: Payment) {
        localDataSource.updatePayment(payment)
    }
    
    override suspend fun deletePayment(payment: Payment) {
        localDataSource.deletePayment(payment)
    }
    
    override fun getPaymentsByStatus(status: PaymentStatus): Flow<List<Payment>> {
        return localDataSource.getPaymentsByStatus(status)
    }
    
    override fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>> {
        return localDataSource.getPaymentsByMethod(method)
    }
    
    override fun getPaymentsByDateRange(startDate: Date, endDate: Date): Flow<List<Payment>> {
        return localDataSource.getPaymentsByDateRange(startDate, endDate)
    }
    
    override suspend fun getTodaysTotalSales(): Double {
        return localDataSource.getTodaysTotalSales() ?: 0.0
    }
    
    override suspend fun getTodaysTransactionCount(): Int {
        return localDataSource.getTodaysTransactionCount()
    }
    
    override suspend fun createTransaction(request: TransactionRequest): NetworkResult<Payment> {
        return try {
            val response = pagBankApiService.createTransaction("", request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val transactionResponse = response.body()?.data
                
                if (transactionResponse != null) {
                    val payment = convertToPayment(transactionResponse)
                    insertPayment(payment)
                    NetworkResult.Success(payment)
                } else {
                    NetworkResult.Error("Resposta inválida do servidor")
                }
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Erro na transação: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Erro de conectividade: ${e.message}")
        }
    }
    
    override suspend fun getTransactionStatus(transactionId: String): NetworkResult<Payment> {
        return try {
            val response = pagBankApiService.getTransaction("", transactionId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val transactionResponse = response.body()?.data
                
                if (transactionResponse != null) {
                    val payment = convertToPayment(transactionResponse)
                    updatePayment(payment)
                    NetworkResult.Success(payment)
                } else {
                    NetworkResult.Error("Transação não encontrada")
                }
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Erro ao consultar transação"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Erro de conectividade: ${e.message}")
        }
    }
    
    override suspend fun cancelTransaction(transactionId: String): NetworkResult<Payment> {
        return try {
            val response = pagBankApiService.cancelTransaction("", transactionId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val transactionResponse = response.body()?.data
                
                if (transactionResponse != null) {
                    val payment = convertToPayment(transactionResponse)
                    updatePayment(payment)
                    NetworkResult.Success(payment)
                } else {
                    NetworkResult.Error("Erro ao cancelar transação")
                }
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Erro ao cancelar transação"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Erro de conectividade: ${e.message}")
        }
    }
    
    override suspend fun syncPaymentsWithServer(): NetworkResult<List<Payment>> {
        return try {
            val response = pagBankApiService.getTransactions("")
            
            if (response.isSuccessful && response.body()?.success == true) {
                val transactions = response.body()?.data ?: emptyList()
                val payments = transactions.map { convertToPayment(it) }
                
                localDataSource.insertPayments(payments)
                NetworkResult.Success(payments)
            } else {
                NetworkResult.Error("Erro ao sincronizar com servidor")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Erro de conectividade: ${e.message}")
        }
    }
    
    private fun convertToPayment(transactionResponse: TransactionResponse): Payment {
        return Payment(
            id = transactionResponse.id,
            amount = transactionResponse.amount,
            paymentMethod = PaymentMethod.values().find { 
                it.value == transactionResponse.paymentMethod 
            } ?: PaymentMethod.CREDIT_CARD,
            status = PaymentStatus.values().find { 
                it.value == transactionResponse.status 
            } ?: PaymentStatus.PENDING,
            transactionId = transactionResponse.id,
            authorizationCode = transactionResponse.authorizationCode,
            receipt = transactionResponse.receiptUrl,
            customerName = null,
            customerDocument = null,
            createdAt = Date(),
            updatedAt = Date(),
            description = null
        )
    }
}
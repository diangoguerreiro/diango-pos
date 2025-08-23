// domain/usecase/GetPaymentHistoryUseCase.kt
package com.diango.pos.domain.usecase

import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetPaymentHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    fun getAllPayments(): Flow<List<Payment>> {
        return paymentRepository.getAllPayments()
    }
    
    fun getPaymentsByStatus(status: PaymentStatus): Flow<List<Payment>> {
        return paymentRepository.getPaymentsByStatus(status)
    }
    
    fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>> {
        return paymentRepository.getPaymentsByMethod(method)
    }
    
    fun getPaymentsByDateRange(startDate: Date, endDate: Date): Flow<List<Payment>> {
        return paymentRepository.getPaymentsByDateRange(startDate, endDate)
    }
}
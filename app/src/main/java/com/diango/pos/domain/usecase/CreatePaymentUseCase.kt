/ domain/usecase/CreatePaymentUseCase.kt
package com.diango.pos.domain.usecase

import com.diango.pos.data.model.Customer
import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.TransactionRequest
import com.diango.pos.domain.repository.PaymentRepository
import com.diango.pos.utils.NetworkResult
import javax.inject.Inject

class CreatePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    suspend operator fun invoke(
        amount: Double,
        paymentMethod: PaymentMethod,
        installments: Int = 1,
        description: String? = null,
        customer: Customer? = null
    ): NetworkResult<Payment> {
        
        // Validações
        if (amount <= 0) {
            return NetworkResult.Error("Valor deve ser maior que zero")
        }
        
        if (installments < 1) {
            return NetworkResult.Error("Número de parcelas inválido")
        }
        
        // PIX não aceita parcelamento
        if (paymentMethod == PaymentMethod.PIX && installments > 1) {
            return NetworkResult.Error("PIX não aceita parcelamento")
        }
        
        val request = TransactionRequest(
            amount = amount,
            paymentMethod = paymentMethod.value,
            installments = installments,
            description = description,
            customer = customer
        )
        
        return paymentRepository.createTransaction(request)
    }
}
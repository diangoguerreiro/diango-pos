// presentation/viewmodel/PaymentViewModel.kt
package com.diango.pos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diango.pos.data.model.Customer
import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.domain.usecase.CreatePaymentUseCase
import com.diango.pos.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createPaymentUseCase: CreatePaymentUseCase
) : ViewModel() {
    
    private val _paymentState = MutableStateFlow<NetworkResult<Payment>>(NetworkResult.Idle())
    val paymentState: StateFlow<NetworkResult<Payment>> = _paymentState.asStateFlow()
    
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CREDIT_CARD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()
    
    private val _installments = MutableStateFlow(1)
    val installments: StateFlow<Int> = _installments.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()
    
    fun setAmount(amount: String) {
        _amount.value = amount
    }
    
    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
        // Reset installments if PIX
        if (method == PaymentMethod.PIX) {
            _installments.value = 1
        }
    }
    
    fun setInstallments(installments: Int) {
        if (_selectedPaymentMethod.value != PaymentMethod.PIX) {
            _installments.value = installments
        }
    }
    
    fun setDescription(description: String) {
        _description.value = description
    }
    
    fun setCustomer(customer: Customer?) {
        _customer.value = customer
    }
    
    fun processPayment() {
        val amountValue = _amount.value.toDoubleOrNull()
        
        if (amountValue == null || amountValue <= 0) {
            _paymentState.value = NetworkResult.Error("Valor inválido")
            return
        }
        
        viewModelScope.launch {
            _paymentState.value = NetworkResult.Loading()
            
            val result = createPaymentUseCase(
                amount = amountValue,
                paymentMethod = _selectedPaymentMethod.value,
                installments = _installments.value,
                description = _description.value.takeIf { it.isNotBlank() },
                customer = _customer.value
            )
            
            _paymentState.value = result
        }
    }
    
    fun resetPaymentState() {
        _paymentState.value = NetworkResult.Idle()
        _amount.value = ""
        _description.value = ""
        _customer.value = null
        _installments.value = 1
    }
    
    fun clearError() {
        if (_paymentState.value is NetworkResult.Error) {
            _paymentState.value = NetworkResult.Idle()
        }
    }
}
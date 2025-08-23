// presentation/viewmodel/HistoryViewModel.kt
package com.diango.pos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.domain.usecase.GetPaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase
) : ViewModel() {
    
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()
    
    private val _filteredPayments = MutableStateFlow<List<Payment>>(emptyList())
    val filteredPayments: StateFlow<List<Payment>> = _filteredPayments.asStateFlow()
    
    private val _selectedStatus = MutableStateFlow<PaymentStatus?>(null)
    val selectedStatus: StateFlow<PaymentStatus?> = _selectedStatus.asStateFlow()
    
    private val _selectedMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedMethod: StateFlow<PaymentMethod?> = _selectedMethod.asStateFlow()
    
    private val _startDate = MutableStateFlow<Date?>(null)
    val startDate: StateFlow<Date?> = _startDate.asStateFlow()
    
    private val _endDate = MutableStateFlow<Date?>(null)
    val endDate: StateFlow<Date?> = _endDate.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadAllPayments()
    }
    
    private fun loadAllPayments() {
        viewModelScope.launch {
            _isLoading.value = true
            getPaymentHistoryUseCase.getAllPayments().collect { paymentList ->
                _payments.value = paymentList
                applyFilters()
                _isLoading.value = false
            }
        }
    }
    
    fun filterByStatus(status: PaymentStatus?) {
        _selectedStatus.value = status
        applyFilters()
    }
    
    fun filterByMethod(method: PaymentMethod?) {
        _selectedMethod.value = method
        applyFilters()
    }
    
    fun filterByDateRange(startDate: Date?, endDate: Date?) {
        _startDate.value = startDate
        _endDate.value = endDate
        applyFilters()
    }
    
    fun clearFilters() {
        _selectedStatus.value = null
        _selectedMethod.value = null
        _startDate.value = null
        _endDate.value = null
        applyFilters()
    }
    
    private fun applyFilters() {
        var filtered = _payments.value
        
        // Filtrar por status
        _selectedStatus.value?.let { status ->
            filtered = filtered.filter { it.status == status }
        }
        
        // Filtrar por método
        _selectedMethod.value?.let { method ->
            filtered = filtered.filter { it.paymentMethod == method }
        }
        
        // Filtrar por data
        if (_startDate.value != null && _endDate.value != null) {
            filtered = filtered.filter { payment ->
                payment.createdAt >= _startDate.value!! && 
                payment.createdAt <= _endDate.value!!
            }
        }
        
        _filteredPayments.value = filtered
    }
    
    fun exportToCSV(): String {
        val sb = StringBuilder()
        sb.append("ID,Valor,Método,Status,Data,Descrição\n")
        
        _filteredPayments.value.forEach { payment ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sb.append("${payment.id},")
            sb.append("${payment.amount},")
            sb.append("${payment.paymentMethod.displayName},")
            sb.append("${payment.status.displayName},")
            sb.append("${dateFormat.format(payment.createdAt)},")
            sb.append("${payment.description ?: ""}\n")
        }
        
        return sb.toString()
    }
}
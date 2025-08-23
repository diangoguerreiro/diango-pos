// presentation/viewmodel/MainViewModel.kt
package com.diango.pos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diango.pos.data.model.Payment
import com.diango.pos.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    
    private val _todaysSales = MutableStateFlow(0.0)
    val todaysSales: StateFlow<Double> = _todaysSales.asStateFlow()
    
    private val _todaysTransactions = MutableStateFlow(0)
    val todaysTransactions: StateFlow<Int> = _todaysTransactions.asStateFlow()
    
    private val _recentPayments = MutableStateFlow<List<Payment>>(emptyList())
    val recentPayments: StateFlow<List<Payment>> = _recentPayments.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadDashboardData()
        observeRecentPayments()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _todaysSales.value = paymentRepository.getTodaysTotalSales()
                _todaysTransactions.value = paymentRepository.getTodaysTransactionCount()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun observeRecentPayments() {
        viewModelScope.launch {
            paymentRepository.getAllPayments().collect { payments ->
                _recentPayments.value = payments.take(5) // Últimas 5 transações
            }
        }
    }
    
    fun refreshDashboard() {
        loadDashboardData()
    }
    
    fun syncWithServer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                paymentRepository.syncPaymentsWithServer()
                loadDashboardData()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
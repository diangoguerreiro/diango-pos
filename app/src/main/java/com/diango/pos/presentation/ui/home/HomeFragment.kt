// presentation/ui/home/HomeFragment.kt
package com.diango.pos.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.diango.pos.R
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.databinding.FragmentHomeBinding
import com.diango.pos.presentation.adapter.RecentPaymentsAdapter
import com.diango.pos.presentation.viewmodel.MainViewModel
import com.diango.pos.utils.CurrencyFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recentPaymentsAdapter: RecentPaymentsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        recentPaymentsAdapter = RecentPaymentsAdapter { payment ->
            // Handle payment item click - navigate to detail or show more info
        }
        
        binding.rvRecentPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentPaymentsAdapter
            isNestedScrollingEnabled = false
        }
    }
    
    private fun setupClickListeners() {
        binding.btnCreditPayment.setOnClickListener {
            navigateToPayment(PaymentMethod.CREDIT_CARD)
        }
        
        binding.btnDebitPayment.setOnClickListener {
            navigateToPayment(PaymentMethod.DEBIT_CARD)
        }
        
        binding.btnPixPayment.setOnClickListener {
            navigateToPayment(PaymentMethod.PIX)
        }
        
        binding.btnVoucherPayment.setOnClickListener {
            navigateToPayment(PaymentMethod.VOUCHER)
        }
        
        binding.btnViewAll.setOnClickListener {
            findNavController().navigate(R.id.navigation_history)
        }
        
        binding.btnSync.setOnClickListener {
            viewModel.syncWithServer()
        }
        
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todaysSales.collect { sales ->
                binding.tvTodaysSales.text = CurrencyFormatter.format(sales)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todaysTransactions.collect { count ->
                binding.tvTodaysTransactions.text = count.toString()
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentPayments.collect { payments ->
                recentPaymentsAdapter.submitList(payments)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun navigateToPayment(paymentMethod: PaymentMethod) {
        val bundle = Bundle().apply {
            putString("preselected_method", paymentMethod.value)
        }
        findNavController().navigate(R.id.navigation_payment, bundle)
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshDashboard()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
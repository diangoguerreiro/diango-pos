// presentation/ui/history/HistoryFragment.kt
package com.diango.pos.presentation.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.diango.pos.R
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.databinding.FragmentHistoryBinding
import com.diango.pos.presentation.adapter.PaymentsAdapter
import com.diango.pos.presentation.viewmodel.HistoryViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.util.*

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var paymentsAdapter: PaymentsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFilterListeners()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        paymentsAdapter = PaymentsAdapter { payment ->
            // Handle payment item click - show details
            PaymentDetailBottomSheet.newInstance(payment)
                .show(parentFragmentManager, "PaymentDetailBottomSheet")
        }
        
        binding.rvPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentsAdapter
        }
    }
    
    private fun setupFilterListeners() {
        // Status filters
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_all_status) || checkedIds.isEmpty() -> {
                    viewModel.filterByStatus(null)
                }
                checkedIds.contains(R.id.chip_approved) -> {
                    viewModel.filterByStatus(PaymentStatus.APPROVED)
                }
                checkedIds.contains(R.id.chip_pending) -> {
                    viewModel.filterByStatus(PaymentStatus.PENDING)
                }
                checkedIds.contains(R.id.chip_declined) -> {
                    viewModel.filterByStatus(PaymentStatus.DECLINED)
                }
            }
        }
        
        // Method filters
        binding.chipGroupMethods.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_all_methods) || checkedIds.isEmpty() -> {
                    viewModel.filterByMethod(null)
                }
                checkedIds.contains(R.id.chip_credit_filter) -> {
                    viewModel.filterByMethod(PaymentMethod.CREDIT_CARD)
                }
                checkedIds.contains(R.id.chip_debit_filter) -> {
                    viewModel.filterByMethod(PaymentMethod.DEBIT_CARD)
                }
                checkedIds.contains(R.id.chip_pix_filter) -> {
                    viewModel.filterByMethod(PaymentMethod.PIX)
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }
        
        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            // Reset chip selections
            binding.chipAllStatus.isChecked = true
            binding.chipAllMethods.isChecked = true
        }
        
        binding.btnExport.setOnClickListener {
            exportTransactions()
        }
        
        binding.fabNewPayment.setOnClickListener {
            findNavController().navigate(R.id.navigation_payment)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredPayments.collect { payments ->
                paymentsAdapter.submitList(payments)
                
                // Show empty state if no payments
                binding.layoutEmpty.visibility = if (payments.isEmpty()) View.VISIBLE else View.GONE
                binding.rvPayments.visibility = if (payments.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecionar período")
            .build()
        
        dateRangePicker.show(parentFragmentManager, "DateRangePicker")
        
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = Date(selection.first)
            val endDate = Date(selection.second)
            viewModel.filterByDateRange(startDate, endDate)
        }
    }
    
    private fun exportTransactions() {
        try {
            val csvData = viewModel.exportToCSV()
            val fileName = "transacoes_${System.currentTimeMillis()}.csv"
            val file = File(requireContext().cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write(csvData)
            }
            
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Exportar transações"))
            
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Erro ao exportar: ${e.message}", Snackbar.LENGTH_LONG)
                .show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
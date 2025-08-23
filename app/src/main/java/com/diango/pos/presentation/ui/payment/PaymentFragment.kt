// presentation/ui/payment/PaymentFragment.kt
package com.diango.pos.presentation.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.diango.pos.R
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.databinding.FragmentPaymentBinding
import com.diango.pos.presentation.viewmodel.PaymentViewModel
import com.diango.pos.utils.CurrencyFormatter
import com.diango.pos.utils.NetworkResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PaymentViewModel by viewModels()
    
    private var currentAmount = ""
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupInstallmentsSpinner()
        setupKeypadListeners()
        setupPaymentMethodListeners()
        setupClickListeners()
        observeViewModel()
        
        // Check for preselected payment method from arguments
        arguments?.getString("preselected_method")?.let { method ->
            val paymentMethod = PaymentMethod.values().find { it.value == method }
            paymentMethod?.let { 
                viewModel.setPaymentMethod(it)
                selectPaymentMethodChip(it)
            }
        }
    }
    
    private fun setupInstallmentsSpinner() {
        val installmentsOptions = (1..12).map { "${it}x" }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            installmentsOptions
        )
        binding.spinnerInstallments.setAdapter(adapter)
        binding.spinnerInstallments.setText("1x", false)
        
        binding.spinnerInstallments.setOnItemClickListener { _, _, position, _ ->
            viewModel.setInstallments(position + 1)
        }
    }
    
    private fun setupKeypadListeners() {
        // Números
        binding.btn1.setOnClickListener { appendDigit("1") }
        binding.btn2.setOnClickListener { appendDigit("2") }
        binding.btn3.setOnClickListener { appendDigit("3") }
        binding.btn4.setOnClickListener { appendDigit("4") }
        binding.btn5.setOnClickListener { appendDigit("5") }
        binding.btn6.setOnClickListener { appendDigit("6") }
        binding.btn7.setOnClickListener { appendDigit("7") }
        binding.btn8.setOnClickListener { appendDigit("8") }
        binding.btn9.setOnClickListener { appendDigit("9") }
        binding.btn0.setOnClickListener { appendDigit("0") }
        
        // Ações
        binding.btnClear.setOnClickListener { clearAmount() }
        binding.btnBackspace.setOnClickListener { removeLastDigit() }
    }
    
    private fun setupPaymentMethodListeners() {
        binding.chipGroupPaymentMethods.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.contains(R.id.chip_credit) -> {
                    viewModel.setPaymentMethod(PaymentMethod.CREDIT_CARD)
                    binding.tilInstallments.visibility = View.VISIBLE
                }
                checkedIds.contains(R.id.chip_debit) -> {
                    viewModel.setPaymentMethod(PaymentMethod.DEBIT_CARD)
                    binding.tilInstallments.visibility = View.GONE
                }
                checkedIds.contains(R.id.chip_pix) -> {
                    viewModel.setPaymentMethod(PaymentMethod.PIX)
                    binding.tilInstallments.visibility = View.GONE
                }
                checkedIds.contains(R.id.chip_voucher) -> {
                    viewModel.setPaymentMethod(PaymentMethod.VOUCHER)
                    binding.tilInstallments.visibility = View.GONE
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnProcessPayment.setOnClickListener {
            val description = binding.etDescription.text.toString()
            viewModel.setDescription(description)
            viewModel.processPayment()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.amount.collect { amount ->
                updateAmountDisplay(amount)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedPaymentMethod.collect { method ->
                selectPaymentMethodChip(method)
                updateInstallmentsVisibility(method)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.installments.collect { installments ->
                binding.spinnerInstallments.setText("${installments}x", false)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paymentState.collect { state ->
                when (state) {
                    is NetworkResult.Loading -> {
                        binding.progressPayment.visibility = View.VISIBLE
                        binding.btnProcessPayment.isEnabled = false
                    }
                    is NetworkResult.Success -> {
                        binding.progressPayment.visibility = View.GONE
                        binding.btnProcessPayment.isEnabled = true
                        state.data?.let { payment ->
                            showPaymentResult(true, payment.id, payment.authorizationCode)
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressPayment.visibility = View.GONE
                        binding.btnProcessPayment.isEnabled = true
                        showPaymentResult(false, null, null, state.message)
                    }
                    is NetworkResult.Idle -> {
                        binding.progressPayment.visibility = View.GONE
                        binding.btnProcessPayment.isEnabled = true
                    }
                }
            }
        }
    }
    
    private fun appendDigit(digit: String) {
        if (currentAmount.length < 10) { // Limit to prevent overflow
            currentAmount += digit
            viewModel.setAmount(formatAmountInput(currentAmount))
        }
    }
    
    private fun removeLastDigit() {
        if (currentAmount.isNotEmpty()) {
            currentAmount = currentAmount.dropLast(1)
            viewModel.setAmount(formatAmountInput(currentAmount))
        }
    }
    
    private fun clearAmount() {
        currentAmount = ""
        viewModel.setAmount("")
    }
    
    private fun formatAmountInput(input: String): String {
        return if (input.isEmpty()) {
            "0.00"
        } else {
            val number = input.toLongOrNull() ?: 0L
            (number / 100.0).toString()
        }
    }
    
    private fun updateAmountDisplay(amount: String) {
        val displayAmount = if (amount.isBlank() || amount == "0.00") {
            "R$ 0,00"
        } else {
            CurrencyFormatter.format(amount.toDoubleOrNull() ?: 0.0)
        }
        binding.tvAmountDisplay.text = displayAmount
    }
    
    private fun selectPaymentMethodChip(method: PaymentMethod) {
        when (method) {
            PaymentMethod.CREDIT_CARD -> binding.chipCredit.isChecked = true
            PaymentMethod.DEBIT_CARD -> binding.chipDebit.isChecked = true
            PaymentMethod.PIX -> binding.chipPix.isChecked = true
            PaymentMethod.VOUCHER -> binding.chipVoucher.isChecked = true
            else -> binding.chipCredit.isChecked = true
        }
    }
    
    private fun updateInstallmentsVisibility(method: PaymentMethod) {
        binding.tilInstallments.visibility = if (method == PaymentMethod.CREDIT_CARD) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
    
    private fun showPaymentResult(
        success: Boolean,
        transactionId: String?,
        authCode: String?,
        errorMessage: String? = null
    ) {
        val dialog = PaymentResultDialog.newInstance(
            success = success,
            transactionId = transactionId,
            authCode = authCode,
            amount = viewModel.amount.value.toDoubleOrNull() ?: 0.0,
            paymentMethod = viewModel.selectedPaymentMethod.value,
            errorMessage = errorMessage
        )
        
        dialog.setOnDismissListener {
            if (success) {
                // Reset form for new payment
                viewModel.resetPaymentState()
                clearAmount()
                binding.etDescription.setText("")
            }
        }
        
        dialog.show(parentFragmentManager, "PaymentResultDialog")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
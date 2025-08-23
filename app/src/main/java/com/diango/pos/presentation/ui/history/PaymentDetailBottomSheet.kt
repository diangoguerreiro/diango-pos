// presentation/ui/history/PaymentDetailBottomSheet.kt
package com.diango.pos.presentation.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diango.pos.R
import com.diango.pos.data.model.Payment
import com.diango.pos.databinding.BottomSheetPaymentDetailBinding
import com.diango.pos.utils.CurrencyFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class PaymentDetailBottomSheet : BottomSheetDialogFragment() {
    
    private var _binding: BottomSheetPaymentDetailBinding? = null
    private val binding get() = _binding!!
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPaymentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val payment = arguments?.getParcelable<Payment>(ARG_PAYMENT)
        payment?.let { setupPaymentDetails(it) }
        
        setupClickListeners()
    }
    
    private fun setupPaymentDetails(payment: Payment) {
        binding.apply {
            // Basic info
            tvPaymentId.text = payment.id
            tvAmount.text = CurrencyFormatter.format(payment.amount)
            tvPaymentMethod.text = payment.paymentMethod.displayName
            tvStatus.text = payment.status.displayName
            tvCreatedAt.text = dateFormat.format(payment.createdAt)
            
            // Optional fields
            if (payment.authorizationCode != null) {
                tvAuthCode.text = payment.authorizationCode
                layoutAuthCode.visibility = View.VISIBLE
            }
            
            if (payment.description != null) {
                tvDescription.text = payment.description
                layoutDescription.visibility = View.VISIBLE
            }
            
            if (payment.customerName != null) {
                tvCustomerName.text = payment.customerName
                layoutCustomerName.visibility = View.VISIBLE
            }
            
            if (payment.customerDocument != null) {
                tvCustomerDocument.text = payment.customerDocument
                layoutCustomerDocument.visibility = View.VISIBLE
            }
            
            // Status-dependent actions
            when (payment.status) {
                com.diango.pos.data.model.PaymentStatus.APPROVED -> {
                    btnCancelPayment.visibility = View.VISIBLE
                    if (payment.receipt != null) {
                        btnPrintReceipt.visibility = View.VISIBLE
                    }
                }
                com.diango.pos.data.model.PaymentStatus.PENDING -> {
                    btnRefreshStatus.visibility = View.VISIBLE
                }
                else -> {
                    // Hide action buttons for other statuses
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        
        binding.btnCancelPayment.setOnClickListener {
            // TODO: Implement payment cancellation
        }
        
        binding.btnPrintReceipt.setOnClickListener {
            // TODO: Implement receipt printing
        }
        
        binding.btnRefreshStatus.setOnClickListener {
            // TODO: Implement status refresh
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_PAYMENT = "payment"
        
        fun newInstance(payment: Payment): PaymentDetailBottomSheet {
            return PaymentDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAYMENT, payment)
                }
            }
        }
    }
}
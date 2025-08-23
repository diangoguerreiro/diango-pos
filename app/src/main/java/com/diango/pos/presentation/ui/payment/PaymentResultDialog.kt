// presentation/ui/payment/PaymentResultDialog.kt
package com.diango.pos.presentation.ui.payment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.diango.pos.R
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.databinding.DialogPaymentResultBinding
import com.diango.pos.utils.CurrencyFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PaymentResultDialog : DialogFragment() {
    
    private var _binding: DialogPaymentResultBinding? = null
    private val binding get() = _binding!!
    
    private var onDismissListener: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPaymentResultBinding.inflate(layoutInflater)
        
        val success = arguments?.getBoolean(ARG_SUCCESS) ?: false
        val transactionId = arguments?.getString(ARG_TRANSACTION_ID)
        val authCode = arguments?.getString(ARG_AUTH_CODE)
        val amount = arguments?.getDouble(ARG_AMOUNT) ?: 0.0
        val paymentMethodValue = arguments?.getString(ARG_PAYMENT_METHOD)
        val errorMessage = arguments?.getString(ARG_ERROR_MESSAGE)
        
        val paymentMethod = PaymentMethod.values().find { it.value == paymentMethodValue }
            ?: PaymentMethod.CREDIT_CARD
        
        setupUI(success, transactionId, authCode, amount, paymentMethod, errorMessage)
        setupClickListeners()
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }
    
    private fun setupUI(
        success: Boolean,
        transactionId: String?,
        authCode: String?,
        amount: Double,
        paymentMethod: PaymentMethod,
        errorMessage: String?
    ) {
        binding.apply {
            if (success) {
                ivResultIcon.setImageResource(R.drawable.ic_check_circle)
                ivResultIcon.setColorFilter(requireContext().getColor(R.color.success))
                tvResultTitle.text = "Pagamento Aprovado!"
                tvResultMessage.text = "Sua transação foi processada com sucesso."
                
                // Show transaction details
                cardTransactionDetails.visibility = android.view.View.VISIBLE
                tvDetailAmount.text = CurrencyFormatter.format(amount)
                tvDetailMethod.text = paymentMethod.displayName
                tvDetailId.text = transactionId ?: "N/A"
                
                if (authCode != null) {
                    layoutAuthCode.visibility = android.view.View.VISIBLE
                    tvDetailAuthCode.text = authCode
                }
                
                btnPrintReceipt.visibility = android.view.View.VISIBLE
                
            } else {
                ivResultIcon.setImageResource(R.drawable.ic_error)
                ivResultIcon.setColorFilter(requireContext().getColor(R.color.error))
                tvResultTitle.text = "Pagamento Recusado"
                tvResultMessage.text = errorMessage ?: "Não foi possível processar o pagamento."
                
                cardTransactionDetails.visibility = android.view.View.GONE
                btnPrintReceipt.visibility = android.view.View.GONE
                btnNewPayment.text = "Tentar Novamente"
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        
        binding.btnNewPayment.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.navigation_payment)
        }
        
        binding.btnPrintReceipt.setOnClickListener {
            // TODO: Implement print receipt functionality
            // This would integrate with the POS printer
        }
    }
    
    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }
    
    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_SUCCESS = "success"
        private const val ARG_TRANSACTION_ID = "transaction_id"
        private const val ARG_AUTH_CODE = "auth_code"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_PAYMENT_METHOD = "payment_method"
        private const val ARG_ERROR_MESSAGE = "error_message"
        
        fun newInstance(
            success: Boolean,
            transactionId: String?,
            authCode: String?,
            amount: Double,
            paymentMethod: PaymentMethod,
            errorMessage: String? = null
        ): PaymentResultDialog {
            return PaymentResultDialog().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_SUCCESS, success)
                    putString(ARG_TRANSACTION_ID, transactionId)
                    putString(ARG_AUTH_CODE, authCode)
                    putDouble(ARG_AMOUNT, amount)
                    putString(ARG_PAYMENT_METHOD, paymentMethod.value)
                    putString(ARG_ERROR_MESSAGE, errorMessage)
                }
            }
        }
    }
}
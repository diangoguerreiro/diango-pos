// presentation/adapter/PaymentsAdapter.kt
package com.diango.pos.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diango.pos.R
import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import com.diango.pos.databinding.ItemPaymentBinding
import com.diango.pos.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class PaymentsAdapter(
    private val onItemClick: (Payment) -> Unit
) : ListAdapter<Payment, PaymentsAdapter.PaymentViewHolder>(PaymentDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PaymentViewHolder(
        private val binding: ItemPaymentBinding,
        private val onItemClick: (Payment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        fun bind(payment: Payment) {
            binding.apply {
                // Set payment info
                tvTransactionId.text = payment.id.takeLast(8) // Show last 8 characters
                tvPaymentMethod.text = payment.paymentMethod.displayName
                tvDateTime.text = dateFormat.format(payment.createdAt)
                tvAmount.text = CurrencyFormatter.format(payment.amount)
                
                // Set payment method icon
                ivPaymentMethod.setImageResource(getPaymentMethodIcon(payment.paymentMethod))
                ivPaymentMethod.setColorFilter(getPaymentMethodColor(payment.paymentMethod))
                
                // Set status chip
                chipStatus.text = payment.status.displayName
                chipStatus.chipBackgroundColor = binding.root.context.getColorStateList(
                    getStatusColor(payment.status)
                )
                
                // Show description if available
                if (payment.description.isNullOrBlank()) {
                    tvDescription.visibility = android.view.View.GONE
                } else {
                    tvDescription.visibility = android.view.View.VISIBLE
                    tvDescription.text = payment.description
                }
                
                // Set click listener
                root.setOnClickListener { onItemClick(payment) }
            }
        }
        
        private fun getPaymentMethodIcon(method: PaymentMethod): Int {
            return when (method) {
                PaymentMethod.CREDIT_CARD -> R.drawable.ic_credit_card
                PaymentMethod.DEBIT_CARD -> R.drawable.ic_debit_card
                PaymentMethod.PIX -> R.drawable.ic_pix
                PaymentMethod.VOUCHER -> R.drawable.ic_voucher
                PaymentMethod.CASH -> R.drawable.ic_cash
            }
        }
        
        private fun getPaymentMethodColor(method: PaymentMethod): Int {
            return binding.root.context.getColor(
                when (method) {
                    PaymentMethod.CREDIT_CARD -> R.color.credit_card
                    PaymentMethod.DEBIT_CARD -> R.color.debit_card
                    PaymentMethod.PIX -> R.color.pix
                    PaymentMethod.VOUCHER -> R.color.voucher
                    PaymentMethod.CASH -> R.color.cash
                }
            )
        }
        
        private fun getStatusColor(status: PaymentStatus): Int {
            return when (status) {
                PaymentStatus.APPROVED -> R.color.status_approved
                PaymentStatus.PENDING -> R.color.status_pending
                PaymentStatus.PROCESSING -> R.color.status_processing
                PaymentStatus.DECLINED -> R.color.status_declined
                PaymentStatus.CANCELLED -> R.color.status_cancelled
                PaymentStatus.ERROR -> R.color.status_error
            }
        }
    }
    
    private class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
}
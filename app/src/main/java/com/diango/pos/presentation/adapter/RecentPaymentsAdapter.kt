// presentation/adapter/RecentPaymentsAdapter.kt
package com.diango.pos.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diango.pos.data.model.Payment
import com.diango.pos.databinding.ItemPaymentBinding
import com.diango.pos.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class RecentPaymentsAdapter(
    private val onItemClick: (Payment) -> Unit
) : ListAdapter<Payment, RecentPaymentsAdapter.RecentPaymentViewHolder>(PaymentDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentPaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentPaymentViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: RecentPaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class RecentPaymentViewHolder(
        private val binding: ItemPaymentBinding,
        private val onItemClick: (Payment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        fun bind(payment: Payment) {
            binding.apply {
                tvTransactionId.text = payment.id.takeLast(6)
                tvPaymentMethod.text = payment.paymentMethod.displayName
                tvDateTime.text = dateFormat.format(payment.createdAt)
                tvAmount.text = CurrencyFormatter.format(payment.amount)
                
                chipStatus.text = payment.status.displayName
                
                // Hide description for compact view
                tvDescription.visibility = android.view.View.GONE
                
                root.setOnClickListener { onItemClick(payment) }
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
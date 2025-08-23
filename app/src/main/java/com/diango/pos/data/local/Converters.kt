/ data/local/Converters.kt
package com.diango.pos.data.local

import androidx.room.TypeConverter
import com.diango.pos.data.model.PaymentMethod
import com.diango.pos.data.model.PaymentStatus
import java.util.Date

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String {
        return method.value
    }
    
    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod {
        return PaymentMethod.values().find { it.value == value } 
            ?: PaymentMethod.CREDIT_CARD
    }
    
    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String {
        return status.value
    }
    
    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return PaymentStatus.values().find { it.value == value } 
            ?: PaymentStatus.PENDING
    }
}
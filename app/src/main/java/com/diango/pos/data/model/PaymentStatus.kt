// data/model/PaymentStatus.kt
package com.diango.pos.data.model

enum class PaymentStatus(val value: String, val displayName: String) {
    PENDING("pending", "Pendente"),
    PROCESSING("processing", "Processando"),
    APPROVED("approved", "Aprovado"),
    DECLINED("declined", "Recusado"),
    CANCELLED("cancelled", "Cancelado"),
    ERROR("error", "Erro")
}

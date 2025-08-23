// data/model/Customer.kt
package com.diango.pos.data.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("document")
    val document: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("phone")
    val phone: String?
)
// data/remote/BackendApiService.kt
package com.diango.pos.data.remote

import com.diango.pos.data.model.Payment
import com.diango.pos.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface BackendApiService {
    
    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<ApiResponse<Map<String, String>>>
    
    @POST("payments")
    suspend fun savePayment(
        @Header("Authorization") token: String,
        @Body payment: Payment
    ): Response<ApiResponse<Payment>>
    
    @GET("payments")
    suspend fun getPayments(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Payment>>>
    
    @GET("payments/{id}")
    suspend fun getPayment(
        @Header("Authorization") token: String,
        @Path("id") paymentId: String
    ): Response<ApiResponse<Payment>>
    
    @PUT("payments/{id}")
    suspend fun updatePayment(
        @Header("Authorization") token: String,
        @Path("id") paymentId: String,
        @Body payment: Payment
    ): Response<ApiResponse<Payment>>
    
    @GET("reports/daily")
    suspend fun getDailyReport(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<ApiResponse<Map<String, Any>>>
}
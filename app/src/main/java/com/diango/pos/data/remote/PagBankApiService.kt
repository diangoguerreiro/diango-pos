// data/remote/PagBankApiService.kt
package com.diango.pos.data.remote

import com.diango.pos.data.model.ApiResponse
import com.diango.pos.data.model.TransactionRequest
import com.diango.pos.data.model.TransactionResponse
import retrofit2.Response
import retrofit2.http.*

interface PagBankApiService {
    
    @POST("charges")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body request: TransactionRequest
    ): Response<ApiResponse<TransactionResponse>>
    
    @GET("charges/{id}")
    suspend fun getTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<ApiResponse<TransactionResponse>>
    
    @POST("charges/{id}/cancel")
    suspend fun cancelTransaction(
        @Header("Authorization") token: String,
        @Path("id") transactionId: String
    ): Response<ApiResponse<TransactionResponse>>
    
    @GET("charges")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 50,
        @Query("page") page: Int = 1,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<ApiResponse<List<TransactionResponse>>>
}
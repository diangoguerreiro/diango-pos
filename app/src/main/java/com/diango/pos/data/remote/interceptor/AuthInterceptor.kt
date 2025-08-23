// data/remote/interceptor/AuthInterceptor.kt
package com.diango.pos.data.remote.interceptor

import com.diango.pos.utils.SecurityManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val securityManager: SecurityManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Adiciona token de autenticação se disponível
        val token = securityManager.getAuthToken()
        
        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
        } else {
            request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
        }
        
        return chain.proceed(authenticatedRequest)
    }
}
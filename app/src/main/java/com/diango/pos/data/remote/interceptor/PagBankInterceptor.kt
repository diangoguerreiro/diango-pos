// data/remote/interceptor/PagBankInterceptor.kt
package com.diango.pos.data.remote.interceptor

import com.diango.pos.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagBankInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        val pagBankRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.PAGBANK_TOKEN}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        
        return chain.proceed(pagBankRequest)
    }
}
// di/NetworkModule.kt
package com.diango.pos.di

import com.diango.pos.BuildConfig
import com.diango.pos.data.remote.BackendApiService
import com.diango.pos.data.remote.PagBankApiService
import com.diango.pos.data.remote.interceptor.AuthInterceptor
import com.diango.pos.data.remote.interceptor.PagBankInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PagBankRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.IS_DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun providePagBankOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        pagBankInterceptor: PagBankInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(pagBankInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @PagBankRetrofit
    fun providePagBankRetrofit(
        gson: Gson,
        pagBankOkHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(pagBankOkHttpClient)
            .build()
    }
    
    @Provides
    @Singleton
    @BackendRetrofit
    fun provideBackendRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.diango.com.br/") // Substituir pela URL real
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
    
    @Provides
    @Singleton
    fun providePagBankApiService(@PagBankRetrofit retrofit: Retrofit): PagBankApiService {
        return retrofit.create(PagBankApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideBackendApiService(@BackendRetrofit retrofit: Retrofit): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }
}

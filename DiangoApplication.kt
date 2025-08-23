// DiangoApplication.kt
package com.diango.pos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DiangoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicialização do SDK PagBank aqui
        initializePagBankSDK()
    }
    
    private fun initializePagBankSDK() {
        try {
            // TODO: Implementar inicialização do SDK PagBank
            // PagBankSDK.initialize(this, BuildConfig.PAGBANK_EMAIL, BuildConfig.PAGBANK_TOKEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
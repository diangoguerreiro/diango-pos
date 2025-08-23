// utils/SecurityManager.kt
package com.diango.pos.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "diango_pos_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(AUTH_TOKEN_KEY, token)
            .apply()
    }
    
    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }
    
    fun clearAuthToken() {
        sharedPreferences.edit()
            .remove(AUTH_TOKEN_KEY)
            .apply()
    }
    
    fun savePagBankCredentials(email: String, token: String) {
        sharedPreferences.edit()
            .putString(PAGBANK_EMAIL_KEY, email)
            .putString(PAGBANK_TOKEN_KEY, token)
            .apply()
    }
    
    fun getPagBankCredentials(): Pair<String?, String?> {
        val email = sharedPreferences.getString(PAGBANK_EMAIL_KEY, null)
        val token = sharedPreferences.getString(PAGBANK_TOKEN_KEY, null)
        return Pair(email, token)
    }
    
    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val PAGBANK_EMAIL_KEY = "pagbank_email"
        private const val PAGBANK_TOKEN_KEY = "pagbank_token"
    }
}
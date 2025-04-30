package com.example.roots.services

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {
    private const val FILE_NAME = "secure_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    private fun getPrefs(context: Context) = EncryptedSharedPreferences.create(
        FILE_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(context: Context, email: String, password: String) {
        getPrefs(context).edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getCredentials(context: Context): Pair<String?, String?> {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_EMAIL, null) to prefs.getString(KEY_PASSWORD, null)
    }

    fun clearCredentials(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}

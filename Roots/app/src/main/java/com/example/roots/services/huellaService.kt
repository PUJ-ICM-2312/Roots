package com.example.roots.services

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

fun showBiometricPrompt(
    context: Context,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(
        context as androidx.fragment.app.FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onAuthError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthError("Huella no reconocida.")
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticación biométrica")
        .setSubtitle("Usa tu huella para continuar")
        .setNegativeButtonText("Cancelar")
        .build()

    biometricPrompt.authenticate(promptInfo)
}

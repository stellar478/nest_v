package com.example.core.security

interface BiometricLockManager {
    fun isBiometricAvailable(): Boolean
    fun authenticateBiometric(onSuccess: () -> Unit, onError: (String) -> Unit)
}

class NestBiometricLockManager : BiometricLockManager {
    override fun isBiometricAvailable(): Boolean = true

    override fun authenticateBiometric(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Architecture placeholder: System BiometricPrompt wrapper
        onSuccess()
    }
}

package com.example.core.model

data class VaultSecurityState(
    val isLocked: Boolean = true,
    val isBiometricEnabled: Boolean = true,
    val autoLockTimeoutMinutes: Int = 5,
    val failedAttempts: Int = 0,
    val lastUnlockedTimestamp: Long = 0L,
    val hardwareKeyBacked: Boolean = true,
    val algorithm: String = "AES-256-GCM"
)

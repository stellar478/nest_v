package com.example.core.security

import java.util.UUID

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    TRUSTED_DEVICES
}

data class UserAccount(
    val email: String,
    val masterPasswordHash: String,
    val recoveryKey: String,
    val isBiometricEnabled: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

data class TrustedDevice(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val ipAddress: String,
    val lastActiveTime: String,
    val isCurrentDevice: Boolean = false,
    val isTrusted: Boolean = true
)

data class UserSession(
    val sessionToken: String,
    val email: String,
    val loginTimeMs: Long = System.currentTimeMillis(),
    val expiryTimeMs: Long = System.currentTimeMillis() + (15 * 60 * 1000) // 15 mins default
) {
    val isValid: Boolean get() = System.currentTimeMillis() < expiryTimeMs
}

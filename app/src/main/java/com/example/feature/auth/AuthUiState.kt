package com.example.feature.auth

import com.example.core.security.AuthMode
import com.example.core.security.TrustedDevice
import com.example.core.security.UserAccount
import com.example.core.security.UserSession

data class AuthUiState(
    val currentMode: AuthMode = AuthMode.LOGIN,
    val emailInput: String = "user@nordic.vault",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val recoveryKeyInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // Session & Security status
    val currentAccount: UserAccount? = null,
    val activeSession: UserSession? = null,
    val isLocked: Boolean = false,
    val isBiometricAvailable: Boolean = true,
    
    // Recovery key generated during registration
    val generatedRecoveryKey: String? = null,
    val showRecoveryKeyDialog: Boolean = false,
    
    // Trusted Devices
    val trustedDevices: List<TrustedDevice> = emptyList()
)

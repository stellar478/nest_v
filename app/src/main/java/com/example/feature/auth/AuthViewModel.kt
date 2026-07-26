package com.example.feature.auth

import androidx.lifecycle.ViewModel
import com.example.core.security.AuthMode
import com.example.core.security.AuthRepository
import com.example.core.security.NestAuthRepository
import com.example.core.security.NestVaultKeyManager
import com.example.core.security.VaultKeyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val authRepository: AuthRepository = NestAuthRepository(),
    private val keyManager: VaultKeyManager = NestVaultKeyManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val account = authRepository.getCurrentAccount()
        val session = authRepository.getCurrentSession()
        val devices = authRepository.getTrustedDevices()

        _uiState.update {
            it.copy(
                currentAccount = account,
                activeSession = session,
                emailInput = account?.email ?: "user@nordic.vault",
                trustedDevices = devices,
                isLocked = session == null || !session.isValid
            )
        }
    }

    fun setMode(mode: AuthMode) {
        _uiState.update {
            it.copy(
                currentMode = mode,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(emailInput = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPasswordInput = confirmPassword, errorMessage = null) }
    }

    fun updateRecoveryKeyInput(key: String) {
        _uiState.update { it.copy(recoveryKeyInput = key, errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = authRepository.login(state.emailInput, state.passwordInput)
        result.onSuccess { session ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    activeSession = session,
                    isLocked = false,
                    passwordInput = "",
                    successMessage = "Session authenticated successfully!"
                )
            }
            onSuccess()
        }.onFailure { ex ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "Authentication failed."
                )
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.passwordInput != state.confirmPasswordInput) {
            _uiState.update { it.copy(errorMessage = "Master passwords do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = authRepository.registerAccount(state.emailInput, state.passwordInput)
        result.onSuccess { (account, recoveryKey) ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentAccount = account,
                    generatedRecoveryKey = recoveryKey,
                    showRecoveryKeyDialog = true,
                    activeSession = authRepository.getCurrentSession(),
                    isLocked = false,
                    passwordInput = "",
                    confirmPasswordInput = "",
                    successMessage = "Account registered securely."
                )
            }
            onSuccess()
        }.onFailure { ex ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "Registration failed."
                )
            }
        }
    }

    fun recoverAccount(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.passwordInput != state.confirmPasswordInput) {
            _uiState.update { it.copy(errorMessage = "New passwords do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = authRepository.recoverAccount(
            email = state.emailInput,
            recoveryKey = state.recoveryKeyInput,
            newMasterPassword = state.passwordInput
        )

        result.onSuccess {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentMode = AuthMode.LOGIN,
                    passwordInput = "",
                    confirmPasswordInput = "",
                    recoveryKeyInput = "",
                    successMessage = "Master Password successfully reset! Please log in."
                )
            }
            onSuccess()
        }.onFailure { ex ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "Account recovery failed."
                )
            }
        }
    }

    fun loginWithBiometrics(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = authRepository.loginWithBiometrics()
        result.onSuccess { session ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    activeSession = session,
                    isLocked = false,
                    successMessage = "Biometric Passcode verified!"
                )
            }
            onSuccess()
        }.onFailure { ex ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "Biometric authentication failed."
                )
            }
        }
    }

    fun revokeTrustedDevice(deviceId: String) {
        val updatedDevices = authRepository.revokeTrustedDevice(deviceId)
        _uiState.update { it.copy(trustedDevices = updatedDevices) }
    }

    fun dismissRecoveryKeyDialog() {
        _uiState.update { it.copy(showRecoveryKeyDialog = false) }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update {
            it.copy(
                activeSession = null,
                isLocked = true,
                currentMode = AuthMode.LOGIN,
                successMessage = "Vault session locked."
            )
        }
    }
}

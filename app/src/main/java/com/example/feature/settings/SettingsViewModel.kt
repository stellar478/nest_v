package com.example.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val isBiometricEnabled: Boolean = true,
    val autoLockTimeoutMinutes: Int = 5,
    val isClipboardProtectionEnabled: Boolean = true,
    val clipboardClearDelaySeconds: Int = 30,
    val argon2MemoryMb: Int = 64,
    val argon2Iterations: Int = 3,
    val isLoginAlertsEnabled: Boolean = true,
    val showChangeMasterPasswordDialog: Boolean = false,
    val showBackupExportDialog: Boolean = false,
    val statusMessage: String? = null
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleBiometric(enabled: Boolean) {
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun setAutoLockTimeout(minutes: Int) {
        _uiState.update { it.copy(autoLockTimeoutMinutes = minutes) }
    }

    fun toggleClipboardProtection(enabled: Boolean) {
        _uiState.update { it.copy(isClipboardProtectionEnabled = enabled) }
    }

    fun setClipboardClearDelay(seconds: Int) {
        _uiState.update { it.copy(clipboardClearDelaySeconds = seconds) }
    }

    fun toggleLoginAlerts(enabled: Boolean) {
        _uiState.update { it.copy(isLoginAlertsEnabled = enabled) }
    }

    fun showChangeMasterPasswordDialog(show: Boolean) {
        _uiState.update { it.copy(showChangeMasterPasswordDialog = show) }
    }

    fun showBackupExportDialog(show: Boolean) {
        _uiState.update { it.copy(showBackupExportDialog = show) }
    }

    fun setStatusMessage(msg: String?) {
        _uiState.update { it.copy(statusMessage = msg) }
    }
}

package com.example.feature.vault

import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import com.example.core.sync.SyncSessionResult

data class VaultUiState(
    val items: List<VaultItem> = emptyList(),
    val trashItems: List<VaultItem> = emptyList(),
    val isShowingTrash: Boolean = false,
    val selectedCategory: VaultCategory = VaultCategory.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isVaultLocked: Boolean = false,
    val totalEncryptedCount: Int = 0,
    val securityScore: Int = 98,
    val vaultHealthStatus: String = "Optimal Hardware Key Protection",
    val lastBackupTime: String = "Today, 08:30 AM",
    val trustedDevicesCount: Int = 3,
    val syncSessionResult: SyncSessionResult? = null,
    val activeConflictLocalItem: VaultItem? = null,
    val activeConflictCopyItem: VaultItem? = null,
    val totalConflictsCount: Int = 0,
    val isSyncing: Boolean = false,
    val syncLogMessage: String? = null
)

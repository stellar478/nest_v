package com.example.feature.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.repository.VaultRepository
import com.example.core.database.repository.VaultRepositoryImpl
import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.core.sync.OfflineSyncManager
import kotlinx.coroutines.delay

class VaultViewModel(
    private val repository: VaultRepository = VaultRepositoryImpl(),
    private val syncManager: OfflineSyncManager = OfflineSyncManager(repository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState(isLoading = true))
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        loadVaultItems()
        loadTrashItems()
    }

    fun selectCategory(category: VaultCategory) {
        _uiState.update { it.copy(selectedCategory = category, isShowingTrash = false) }
        loadVaultItems()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadVaultItems()
        } else {
            repository.searchItems(query).onEach { items ->
                _uiState.update { it.copy(items = items, isLoading = false) }
            }.launchIn(viewModelScope)
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
            loadVaultItems()
        }
    }

    fun toggleVaultLock() {
        _uiState.update { it.copy(isVaultLocked = !it.isVaultLocked) }
    }

    fun toggleShowingTrash() {
        val newShowingTrash = !_uiState.value.isShowingTrash
        _uiState.update { it.copy(isShowingTrash = newShowingTrash) }
        if (newShowingTrash) {
            loadTrashItems()
        } else {
            loadVaultItems()
        }
    }

    fun softDelete(id: String) {
        viewModelScope.launch {
            repository.softDeleteItem(id)
            loadVaultItems()
            loadTrashItems()
        }
    }

    fun restoreFromTrash(id: String) {
        viewModelScope.launch {
            repository.restoreItem(id)
            loadVaultItems()
            loadTrashItems()
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
            loadTrashItems()
        }
    }

    fun triggerBackup() {
        val currentTime = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date())
        _uiState.update {
            it.copy(lastBackupTime = "Just now ($currentTime)")
        }
    }

    /**
     * Executes offline-first sync.
     * Export encrypted bundle -> Process incoming encrypted payloads.
     */
    fun triggerOfflineSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncLogMessage = "Exporting AES-256 encrypted payloads...") }
            delay(600)
            val bundle = syncManager.exportEncryptedSyncBundle()

            _uiState.update { it.copy(syncLogMessage = "Exchanging ciphertexts with offline peers...") }
            delay(800)

            val result = syncManager.processIncomingEncryptedPackets(bundle)
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            _uiState.update {
                it.copy(
                    isSyncing = false,
                    syncSessionResult = result,
                    syncLogMessage = "Offline Sync complete at $currentTime • Synced ${result.totalItemsSynced} encrypted items."
                )
            }
            loadVaultItems()
        }
    }

    /**
     * Simulates a remote device conflict for testing.
     */
    fun simulateConflict(targetItemId: String) {
        viewModelScope.launch {
            syncManager.simulateRemoteConflict(targetItemId)
            loadVaultItems()
        }
    }

    /**
     * Opens conflict resolution modal comparing local vs conflict copy.
     */
    fun openConflictResolver(localItem: VaultItem) {
        val allItems = _uiState.value.items
        val conflictCopy = allItems.find { it.isConflictCopy && it.originalItemId == localItem.id }
        _uiState.update {
            it.copy(
                activeConflictLocalItem = localItem,
                activeConflictCopyItem = conflictCopy
            )
        }
    }

    fun closeConflictResolver() {
        _uiState.update {
            it.copy(
                activeConflictLocalItem = null,
                activeConflictCopyItem = null
            )
        }
    }

    fun resolveKeepLocal(originalId: String, conflictCopyId: String) {
        viewModelScope.launch {
            syncManager.resolveConflictKeepLocal(originalId, conflictCopyId)
            closeConflictResolver()
            loadVaultItems()
        }
    }

    fun resolveKeepRemote(originalId: String, conflictCopyId: String) {
        viewModelScope.launch {
            syncManager.resolveConflictKeepRemote(originalId, conflictCopyId)
            closeConflictResolver()
            loadVaultItems()
        }
    }

    fun resolveMerge(
        originalId: String,
        conflictCopyId: String,
        mergedTitle: String,
        mergedSubtitle: String,
        mergedPayload: String
    ) {
        viewModelScope.launch {
            syncManager.resolveConflictMerge(originalId, conflictCopyId, mergedTitle, mergedSubtitle, mergedPayload)
            closeConflictResolver()
            loadVaultItems()
        }
    }

    private fun loadVaultItems() {
        val category = _uiState.value.selectedCategory
        repository.getItemsByCategory(category).onEach { items ->
            val conflictCount = items.count { it.isConflictCopy || it.hasUnresolvedConflict }
            _uiState.update {
                it.copy(
                    items = items,
                    totalEncryptedCount = items.size,
                    totalConflictsCount = conflictCount,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadTrashItems() {
        repository.getTrashItems().onEach { trashItems ->
            _uiState.update {
                it.copy(trashItems = trashItems)
            }
        }.launchIn(viewModelScope)
    }
}

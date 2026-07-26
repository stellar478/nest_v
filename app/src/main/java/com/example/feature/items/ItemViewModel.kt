package com.example.feature.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.database.repository.VaultRepository
import com.example.core.database.repository.VaultRepositoryImpl
import com.example.core.model.SecurityGrade
import com.example.core.model.SyncStatus
import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import com.example.core.model.VaultVersionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemUiState(
    val item: VaultItem? = null,
    val title: String = "",
    val subtitle: String = "",
    val category: VaultCategory = VaultCategory.EMAILS,
    val payload: String = "",
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val newTagInput: String = "",
    val versionHistory: List<VaultVersionRecord> = emptyList(),
    val isPayloadVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val isDeletedSuccess: Boolean = false
)

class ItemViewModel(
    private val repository: VaultRepository = VaultRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUiState())
    val uiState: StateFlow<ItemUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: String?) {
        if (itemId == null || itemId.isBlank()) {
            _uiState.update {
                it.copy(
                    item = null,
                    title = "",
                    subtitle = "",
                    category = VaultCategory.EMAILS,
                    payload = "",
                    isFavorite = false,
                    tags = listOf("Primary", "Vault"),
                    versionHistory = emptyList(),
                    isLoading = false
                )
            }
            return
        }

        val validId: String = itemId
        _uiState.update { it.copy(isLoading = true) }
        repository.getItemById(validId).onEach { item ->
            if (item != null) {
                _uiState.update {
                    it.copy(
                        item = item,
                        title = item.title,
                        subtitle = item.subtitle,
                        category = item.category,
                        payload = item.encryptedPayload,
                        isFavorite = item.isFavorite,
                        tags = item.tags,
                        versionHistory = item.versionHistory,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }

    fun updateTitle(value: String) { _uiState.update { it.copy(title = value) } }
    fun updateSubtitle(value: String) { _uiState.update { it.copy(subtitle = value) } }
    fun updateCategory(category: VaultCategory) { _uiState.update { it.copy(category = category) } }
    fun updatePayload(value: String) { _uiState.update { it.copy(payload = value) } }
    fun updateNewTagInput(value: String) { _uiState.update { it.copy(newTagInput = value) } }
    fun togglePayloadVisibility() { _uiState.update { it.copy(isPayloadVisible = !it.isPayloadVisible) } }

    fun addTag() {
        val tag = _uiState.value.newTagInput.trim()
        if (tag.isNotEmpty() && !_uiState.value.tags.contains(tag)) {
            val updated = _uiState.value.tags + tag
            _uiState.update { it.copy(tags = updated, newTagInput = "") }
        }
    }

    fun removeTag(tag: String) {
        val updated = _uiState.value.tags.filterNot { it == tag }
        _uiState.update { it.copy(tags = updated) }
    }

    fun toggleFavorite() {
        val newFav = !_uiState.value.isFavorite
        _uiState.update { it.copy(isFavorite = newFav) }
        _uiState.value.item?.let { existingItem ->
            viewModelScope.launch {
                repository.toggleFavorite(existingItem.id)
            }
        }
    }

    fun saveItem(existingId: String?) {
        val current = _uiState.value
        if (current.title.isBlank()) return

        val itemToSave = current.item?.copy(
            title = current.title,
            subtitle = current.subtitle,
            category = current.category,
            encryptedPayload = current.payload,
            isFavorite = current.isFavorite,
            tags = current.tags,
            lastModifiedAt = System.currentTimeMillis()
        ) ?: VaultItem(
            id = existingId?.takeIf { it.isNotBlank() } ?: java.util.UUID.randomUUID().toString(),
            title = current.title,
            subtitle = current.subtitle,
            category = current.category,
            encryptedPayload = current.payload,
            isFavorite = current.isFavorite,
            tags = current.tags,
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        )

        viewModelScope.launch {
            repository.saveItem(itemToSave)
            _uiState.update { it.copy(isSavedSuccess = true) }
        }
    }

    fun restoreVersion(version: VaultVersionRecord) {
        _uiState.update {
            it.copy(
                title = version.titleSnapshot,
                subtitle = version.subtitleSnapshot,
                payload = version.payloadSnapshot
            )
        }
    }

    fun softDeleteItem(itemId: String) {
        viewModelScope.launch {
            repository.softDeleteItem(itemId)
            _uiState.update { it.copy(isDeletedSuccess = true) }
        }
    }
}

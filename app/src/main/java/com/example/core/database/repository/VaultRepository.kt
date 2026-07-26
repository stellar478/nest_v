package com.example.core.database.repository

import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import kotlinx.coroutines.flow.Flow

interface VaultRepository {
    fun getAllItems(): Flow<List<VaultItem>>
    fun getItemsByCategory(category: VaultCategory): Flow<List<VaultItem>>
    fun getFavoriteItems(): Flow<List<VaultItem>>
    fun getTrashItems(): Flow<List<VaultItem>>
    fun getItemById(id: String): Flow<VaultItem?>
    fun searchItems(query: String): Flow<List<VaultItem>>
    suspend fun saveItem(item: VaultItem)
    suspend fun toggleFavorite(id: String)
    suspend fun softDeleteItem(id: String)
    suspend fun restoreItem(id: String)
    suspend fun deleteItem(id: String)
    suspend fun emptyTrash()
}

package com.example.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items WHERE isDeleted = 0 ORDER BY lastModifiedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE isDeleted = 0 AND categoryName = :category ORDER BY lastModifiedAt DESC")
    fun getVaultItemsByCategory(category: String): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE isDeleted = 0 AND isFavorite = 1 ORDER BY lastModifiedAt DESC")
    fun getFavoriteVaultItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getTrashVaultItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE id = :id LIMIT 1")
    fun getVaultItemById(id: String): Flow<VaultItemEntity?>

    @Query("SELECT * FROM vault_items WHERE isDeleted = 0 AND (title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%' OR tagsJson LIKE '%' || :query || '%' OR encryptedPayload LIKE '%' || :query || '%') ORDER BY lastModifiedAt DESC")
    fun searchVaultItems(query: String): Flow<List<VaultItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultItemEntity)

    @Query("UPDATE vault_items SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteVaultItem(id: String, timestamp: Long)

    @Query("UPDATE vault_items SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreVaultItem(id: String)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteVaultItemById(id: String)

    @Query("DELETE FROM vault_items WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM vault_items")
    suspend fun clearAll()
}

package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val categoryName: String,
    val encryptedPayload: String,
    val iv: String,
    val salt: String,
    val isFavorite: Boolean,
    val tagsJson: String = "[]",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val versionHistoryJson: String = "[]",
    val securityGrade: String,
    val syncStatus: String,
    val createdAt: Long,
    val lastModifiedAt: Long,
    val versionNumber: Int = 1,
    val modifiedTimestamp: Long = System.currentTimeMillis(),
    val deviceId: String = "device_nordic_pixel_8",
    val isConflictCopy: Boolean = false,
    val originalItemId: String? = null,
    val conflictReason: String? = null,
    val hasUnresolvedConflict: Boolean = false
)

package com.example.core.sync

import com.example.core.database.repository.VaultRepository
import com.example.core.database.repository.VaultRepositoryImpl
import com.example.core.model.SecurityGrade
import com.example.core.model.SyncStatus
import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import com.example.core.model.VaultVersionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Encrypted Packet for Syncing between devices.
 * CRITICAL REQUIREMENT: Syncs ONLY encrypted payloads (AES-256 ciphertexts + IV/Salt).
 * Plaintext secrets are NEVER included.
 */
data class EncryptedSyncPayload(
    val itemId: String,
    val title: String,
    val subtitle: String,
    val categoryName: String,
    val encryptedPayload: String, // Ciphertext
    val iv: String,
    val salt: String,
    val versionNumber: Int,
    val modifiedTimestamp: Long,
    val deviceId: String,
    val securityGrade: String
)

data class SyncSessionResult(
    val syncTimestamp: Long = System.currentTimeMillis(),
    val totalItemsSynced: Int = 0,
    val conflictsCreated: Int = 0,
    val logMessage: String = ""
)

class OfflineSyncManager(
    private val vaultRepository: VaultRepository = VaultRepositoryImpl(),
    val currentDeviceId: String = "device_nordic_pixel_8"
) {

    private val _syncState = MutableStateFlow(SyncSessionResult())
    val syncState: StateFlow<SyncSessionResult> = _syncState.asStateFlow()

    /**
     * Serializes local vault items into encrypted sync payloads.
     * Guaranteed to include ONLY encrypted ciphertexts.
     */
    suspend fun exportEncryptedSyncBundle(): String {
        val items = vaultRepository.getAllItems().first()
        val jsonArray = JSONArray()

        items.filterNot { it.isDeleted || it.isConflictCopy }.forEach { item ->
            val jsonObject = JSONObject().apply {
                put("itemId", item.id)
                put("title", item.title)
                put("subtitle", item.subtitle)
                put("categoryName", item.category.name)
                put("encryptedPayload", item.encryptedPayload) // ENCRYPTED CIPHERTEXT ONLY
                put("iv", item.iv)
                put("salt", item.salt)
                put("versionNumber", item.versionNumber)
                put("modifiedTimestamp", item.modifiedTimestamp)
                put("deviceId", item.deviceId)
                put("securityGrade", item.securityGrade.name)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString(2)
    }

    /**
     * Processes incoming encrypted sync packets.
     * Follows strict offline-first conflict rules:
     * 1. Never overwrite data when concurrent edits/versions collide.
     * 2. Create a conflict copy when a conflict occurs.
     */
    suspend fun processIncomingEncryptedPackets(jsonBundle: String): SyncSessionResult {
        var syncedCount = 0
        var conflictsCount = 0
        val currentLocalItems = vaultRepository.getAllItems().first().associateBy { it.id }

        val jsonArray = JSONArray(jsonBundle)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val remotePayload = EncryptedSyncPayload(
                itemId = obj.getString("itemId"),
                title = obj.getString("title"),
                subtitle = obj.optString("subtitle", ""),
                categoryName = obj.optString("categoryName", VaultCategory.EMAILS.name),
                encryptedPayload = obj.getString("encryptedPayload"),
                iv = obj.optString("iv", "nest_iv_def"),
                salt = obj.optString("salt", "nest_salt_def"),
                versionNumber = obj.getInt("versionNumber"),
                modifiedTimestamp = obj.getLong("modifiedTimestamp"),
                deviceId = obj.getString("deviceId"),
                securityGrade = obj.optString("securityGrade", SecurityGrade.STRONG.name)
            )

            val existingLocal = currentLocalItems[remotePayload.itemId]

            if (existingLocal == null) {
                // New item from remote sync - insert directly as encrypted entry
                val category = runCatching { VaultCategory.valueOf(remotePayload.categoryName) }.getOrDefault(VaultCategory.LOGIN)
                val newItem = VaultItem(
                    id = remotePayload.itemId,
                    title = remotePayload.title,
                    subtitle = remotePayload.subtitle,
                    category = category,
                    encryptedPayload = remotePayload.encryptedPayload,
                    iv = remotePayload.iv,
                    salt = remotePayload.salt,
                    versionNumber = remotePayload.versionNumber,
                    modifiedTimestamp = remotePayload.modifiedTimestamp,
                    deviceId = remotePayload.deviceId,
                    securityGrade = runCatching { SecurityGrade.valueOf(remotePayload.securityGrade) }.getOrDefault(SecurityGrade.STRONG),
                    syncStatus = SyncStatus.SYNCED
                )
                vaultRepository.saveItem(newItem)
                syncedCount++
            } else {
                // Item exists locally. Compare versions and timestamps.
                val isSameDevice = existingLocal.deviceId == remotePayload.deviceId
                val isStrictlyNewerSameDevice = isSameDevice && remotePayload.versionNumber > existingLocal.versionNumber

                if (isStrictlyNewerSameDevice) {
                    // Safe update from same device
                    val category = runCatching { VaultCategory.valueOf(remotePayload.categoryName) }.getOrDefault(VaultCategory.LOGIN)
                    val updated = existingLocal.copy(
                        title = remotePayload.title,
                        subtitle = remotePayload.subtitle,
                        category = category,
                        encryptedPayload = remotePayload.encryptedPayload,
                        versionNumber = remotePayload.versionNumber,
                        modifiedTimestamp = remotePayload.modifiedTimestamp,
                        syncStatus = SyncStatus.SYNCED
                    )
                    vaultRepository.saveItem(updated)
                    syncedCount++
                } else if (existingLocal.versionNumber != remotePayload.versionNumber ||
                    existingLocal.encryptedPayload != remotePayload.encryptedPayload ||
                    (!isSameDevice && existingLocal.modifiedTimestamp != remotePayload.modifiedTimestamp)) {

                    // CONFLICT DETECTED!
                    // Rule 1: Never overwrite data.
                    // Rule 2: Create a conflict copy.
                    conflictsCount++
                    val category = runCatching { VaultCategory.valueOf(remotePayload.categoryName) }.getOrDefault(VaultCategory.LOGIN)
                    val conflictCopyId = "${existingLocal.id}_conflict_${UUID.randomUUID().toString().take(6)}"

                    val conflictCopy = VaultItem(
                        id = conflictCopyId,
                        title = "${remotePayload.title} (Conflict - Device: ${remotePayload.deviceId})",
                        subtitle = remotePayload.subtitle,
                        category = category,
                        encryptedPayload = remotePayload.encryptedPayload,
                        iv = remotePayload.iv,
                        salt = remotePayload.salt,
                        versionNumber = remotePayload.versionNumber,
                        modifiedTimestamp = remotePayload.modifiedTimestamp,
                        deviceId = remotePayload.deviceId,
                        isConflictCopy = true,
                        originalItemId = existingLocal.id,
                        conflictReason = "Concurrent edit from device '${remotePayload.deviceId}' (v${remotePayload.versionNumber}) vs local device '${existingLocal.deviceId}' (v${existingLocal.versionNumber})",
                        syncStatus = SyncStatus.PENDING
                    )

                    // Mark local item as having unresolved conflict
                    val updatedLocal = existingLocal.copy(
                        hasUnresolvedConflict = true,
                        syncStatus = SyncStatus.PENDING
                    )

                    vaultRepository.saveItem(updatedLocal)
                    vaultRepository.saveItem(conflictCopy)
                }
            }
        }

        val result = SyncSessionResult(
            syncTimestamp = System.currentTimeMillis(),
            totalItemsSynced = syncedCount,
            conflictsCreated = conflictsCount,
            logMessage = "Offline Sync Complete • Processed ${jsonArray.length()} encrypted payloads. Created $conflictsCount conflict copies."
        )
        _syncState.value = result
        return result
    }

    /**
     * Option A: Keep Local Version.
     * Deletes the conflict copy and clears the unresolved conflict flag on the original item.
     */
    suspend fun resolveConflictKeepLocal(originalItemId: String, conflictCopyId: String) {
        vaultRepository.deleteItem(conflictCopyId)
        val allItems = vaultRepository.getAllItems().first()
        val remainingConflicts = allItems.count { it.isConflictCopy && it.originalItemId == originalItemId }
        if (remainingConflicts <= 1) { // includes the one being deleted
            val original = vaultRepository.getItemById(originalItemId).first()
            if (original != null) {
                vaultRepository.saveItem(original.copy(hasUnresolvedConflict = false))
            }
        }
    }

    /**
     * Option B: Keep Remote (Conflict Copy) Version.
     * Replaces original item's content with the conflict copy's payload, increments version number, and deletes conflict copy.
     */
    suspend fun resolveConflictKeepRemote(originalItemId: String, conflictCopyId: String) {
        val original = vaultRepository.getItemById(originalItemId).first()
        val conflictCopy = vaultRepository.getItemById(conflictCopyId).first()

        if (original != null && conflictCopy != null) {
            val updatedOriginal = original.copy(
                title = conflictCopy.title.replace(" (Conflict - Device: ${conflictCopy.deviceId})", ""),
                subtitle = conflictCopy.subtitle,
                encryptedPayload = conflictCopy.encryptedPayload,
                category = conflictCopy.category,
                versionNumber = maxOf(original.versionNumber, conflictCopy.versionNumber) + 1,
                modifiedTimestamp = System.currentTimeMillis(),
                deviceId = currentDeviceId,
                hasUnresolvedConflict = false
            )
            vaultRepository.saveItem(updatedOriginal)
            vaultRepository.deleteItem(conflictCopyId)
        }
    }

    /**
     * Option C: Merge Versions into a new single item.
     * User specifies merged title, subtitle, and encrypted payload.
     */
    suspend fun resolveConflictMerge(
        originalItemId: String,
        conflictCopyId: String,
        mergedTitle: String,
        mergedSubtitle: String,
        mergedEncryptedPayload: String
    ) {
        val original = vaultRepository.getItemById(originalItemId).first()
        val conflictCopy = vaultRepository.getItemById(conflictCopyId).first()

        if (original != null) {
            val maxVer = maxOf(original.versionNumber, conflictCopy?.versionNumber ?: 1)
            val mergedItem = original.copy(
                title = mergedTitle,
                subtitle = mergedSubtitle,
                encryptedPayload = mergedEncryptedPayload,
                versionNumber = maxVer + 1,
                modifiedTimestamp = System.currentTimeMillis(),
                deviceId = currentDeviceId,
                hasUnresolvedConflict = false
            )
            vaultRepository.saveItem(mergedItem)
            vaultRepository.deleteItem(conflictCopyId)
        }
    }

    /**
     * Utility to generate a simulated conflict copy for demonstration & verification.
     */
    suspend fun simulateRemoteConflict(targetItemId: String, remoteDeviceName: String = "MacBook Pro M2") {
        val target = vaultRepository.getItemById(targetItemId).first() ?: return
        val conflictCopyId = "${target.id}_conflict_${UUID.randomUUID().toString().take(6)}"

        val conflictCopy = VaultItem(
            id = conflictCopyId,
            title = "${target.title} (Conflict - Device: $remoteDeviceName)",
            subtitle = "${target.subtitle} (Remote Edit)",
            category = target.category,
            encryptedPayload = "${target.encryptedPayload}_remote_modified_payload",
            iv = target.iv,
            salt = target.salt,
            versionNumber = target.versionNumber + 1,
            modifiedTimestamp = System.currentTimeMillis() - 3600000L,
            deviceId = "device_${remoteDeviceName.lowercase().replace(" ", "_")}",
            isConflictCopy = true,
            originalItemId = target.id,
            conflictReason = "Simulated concurrent edit from $remoteDeviceName"
        )

        val updatedTarget = target.copy(hasUnresolvedConflict = true)
        vaultRepository.saveItem(updatedTarget)
        vaultRepository.saveItem(conflictCopy)
    }
}

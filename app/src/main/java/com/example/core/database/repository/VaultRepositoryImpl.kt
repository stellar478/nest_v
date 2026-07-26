package com.example.core.database.repository

import com.example.core.database.VaultDao
import com.example.core.database.VaultItemEntity
import com.example.core.model.SecurityGrade
import com.example.core.model.SyncStatus
import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import com.example.core.model.VaultVersionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class VaultRepositoryImpl(
    private val vaultDao: VaultDao? = null
) : VaultRepository {

    // Pre-populated sample items for all 10 container types with tags & version history
    private val mockVaultItems = mutableListOf(
        // 1. EMAILS
        VaultItem(
            id = "email_01",
            title = "Primary ProtonMail",
            subtitle = "vault.master@proton.me",
            category = VaultCategory.EMAILS,
            encryptedPayload = "p_app_sec_993810293_alpha",
            isFavorite = true,
            tags = listOf("Primary", "Encrypted", "Proton"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 2,
                    timestamp = System.currentTimeMillis() - 86400000L * 3,
                    titleSnapshot = "Primary ProtonMail",
                    subtitleSnapshot = "vault.master@proton.me",
                    payloadSnapshot = "p_app_sec_993810293_alpha",
                    changeNote = "Updated 2FA App Key & Recovery Email"
                ),
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 30,
                    titleSnapshot = "Proton Mailbox Initial",
                    subtitleSnapshot = "vault.master@proton.me",
                    payloadSnapshot = "p_app_sec_100000_legacy",
                    changeNote = "Initial Container Creation"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 2. BANKING
        VaultItem(
            id = "banking_01",
            title = "Nordic Federal Vault Account",
            subtitle = "IBAN: NO93 8841 0029 4810 • BIC: NORDFEN1",
            category = VaultCategory.BANKING,
            encryptedPayload = "PIN: 9812 | Swift: NORDFEN1 | Routing: 092841",
            isFavorite = true,
            tags = listOf("Banking", "Primary", "Nordic"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 15,
                    titleSnapshot = "Nordic Federal Vault Account",
                    subtitleSnapshot = "IBAN: NO93 8841 0029 4810",
                    payloadSnapshot = "PIN: 9812",
                    changeNote = "Added IBAN & Routing Information"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 3. CARDS
        VaultItem(
            id = "card_01",
            title = "Platinum Vault Metal Card",
            subtitle = "•••• •••• •••• 8842 | Exp: 09/29",
            category = VaultCategory.CARDS,
            encryptedPayload = "Card: 4532 8810 9928 8842 | CVV: 891 | PIN: 4402",
            isFavorite = true,
            tags = listOf("Travel", "Metal Card", "Finance"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 45,
                    titleSnapshot = "Platinum Vault Metal Card",
                    subtitleSnapshot = "•••• 8842",
                    payloadSnapshot = "CVV: 891",
                    changeNote = "Card Issued and Encrypted"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 4. SOCIAL MEDIA
        VaultItem(
            id = "social_01",
            title = "X / Twitter Vault Account",
            subtitle = "@vault_architect",
            category = VaultCategory.SOCIAL_MEDIA,
            encryptedPayload = "Pass: x_sec_3829!#_token | 2FA: JBSWY3DPEHPK3PXP",
            isFavorite = false,
            tags = listOf("Social", "Verified", "Twitter"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 10,
                    titleSnapshot = "X / Twitter Vault Account",
                    subtitleSnapshot = "@vault_architect",
                    payloadSnapshot = "Pass: x_sec_3829!#_token",
                    changeNote = "Added 2FA Secret Key"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 5. GAMING
        VaultItem(
            id = "gaming_01",
            title = "Steam Hardware Vault",
            subtitle = "Steam ID: NordicGamerX",
            category = VaultCategory.GAMING,
            encryptedPayload = "Pass: steam_p_994821! | Guard Code: R884X",
            isFavorite = false,
            tags = listOf("Gaming", "Steam", "Library"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 60,
                    titleSnapshot = "Steam Hardware Vault",
                    subtitleSnapshot = "NordicGamerX",
                    payloadSnapshot = "Pass: steam_p_994821!",
                    changeNote = "Steam Guard Credentials Stored"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 6. APPS
        VaultItem(
            id = "app_01",
            title = "GitHub Developer Token & SSH",
            subtitle = "ghp_984120391823908123908",
            category = VaultCategory.APPS,
            encryptedPayload = "SSH Fingerprint: SHA256:e981203981029312 | Token: ghp_9841203",
            isFavorite = true,
            tags = listOf("Dev", "GitHub", "SSH"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 2,
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    titleSnapshot = "GitHub Developer Token & SSH",
                    subtitleSnapshot = "ghp_984120391823908123908",
                    payloadSnapshot = "Token Rotated",
                    changeNote = "Rotated Access Token"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 7. DOCUMENTS
        VaultItem(
            id = "doc_01",
            title = "Encrypted Residence Permit Document",
            subtitle = "Scandi Residence No. NO-2026-88192",
            category = VaultCategory.DOCUMENTS,
            encryptedPayload = "Doc Type: National Residence | SHA256: a8f91c920192831",
            isFavorite = false,
            tags = listOf("Official", "Legal", "Identity"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 90,
                    titleSnapshot = "Encrypted Residence Permit",
                    subtitleSnapshot = "NO-2026-88192",
                    payloadSnapshot = "Initial Scan",
                    changeNote = "Encrypted Copy Saved"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 8. RECOVERY CODES
        VaultItem(
            id = "recovery_01",
            title = "Google 2FA Emergency Backup Codes",
            subtitle = "10 One-time Emergency Access Tokens",
            category = VaultCategory.RECOVERY_CODES,
            encryptedPayload = "Codes: 8821-4412, 0912-3381, 1102-9982, 4481-2291, 5510-9921",
            isFavorite = true,
            tags = listOf("2FA", "Emergency", "Backup"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 20,
                    titleSnapshot = "Google 2FA Emergency Backup Codes",
                    subtitleSnapshot = "10 One-time Codes",
                    payloadSnapshot = "10 Codes Saved",
                    changeNote = "Initial Generation"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 9. NOTES
        VaultItem(
            id = "note_01",
            title = "Hardware Vault Master Recovery Seed",
            subtitle = "24-word BIP39 mnemonic phrase",
            category = VaultCategory.NOTES,
            encryptedPayload = "1. alpha 2. brave 3. charlie 4. delta 5. echo 6. foxtrot 7. golf 8. hotel 9. india 10. juliet 11. kilo 12. lima ...",
            isFavorite = true,
            tags = listOf("Crypto", "Seed", "Critical"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 100,
                    titleSnapshot = "Hardware Vault Master Recovery Seed",
                    subtitleSnapshot = "24-word phrase",
                    payloadSnapshot = "24 Words",
                    changeNote = "Air-gapped Key Import"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        ),
        // 10. CONTACTS
        VaultItem(
            id = "contact_01",
            title = "Emergency Vault Advocate Contact",
            subtitle = "Dr. Erik Vane • Legal Guardian",
            category = VaultCategory.CONTACTS,
            encryptedPayload = "Phone: +47 810 99 201 | Email: vane@legal.no | Secure Signal: @erik_vane",
            isFavorite = false,
            tags = listOf("Legal", "Emergency", "Contact"),
            versionHistory = listOf(
                VaultVersionRecord(
                    versionNumber = 1,
                    timestamp = System.currentTimeMillis() - 86400000L * 12,
                    titleSnapshot = "Emergency Vault Advocate Contact",
                    subtitleSnapshot = "Dr. Erik Vane",
                    payloadSnapshot = "Phone & Signal Added",
                    changeNote = "Encrypted Contact Record Created"
                )
            ),
            securityGrade = SecurityGrade.STRONG,
            syncStatus = SyncStatus.OFFLINE_ONLY
        )
    )

    override fun getAllItems(): Flow<List<VaultItem>> {
        return vaultDao?.getAllVaultItems()?.map { list ->
            list.map { it.toDomainModel() }
        } ?: flowOf(mockVaultItems.filter { !it.isDeleted })
    }

    override fun getItemsByCategory(category: VaultCategory): Flow<List<VaultItem>> {
        if (category == VaultCategory.ALL) return getAllItems()
        return vaultDao?.getVaultItemsByCategory(category.name)?.map { list ->
            list.map { it.toDomainModel() }
        } ?: flowOf(mockVaultItems.filter { !it.isDeleted && (it.category == category || it.category.name == category.name) })
    }

    override fun getFavoriteItems(): Flow<List<VaultItem>> {
        return vaultDao?.getFavoriteVaultItems()?.map { list ->
            list.map { it.toDomainModel() }
        } ?: flowOf(mockVaultItems.filter { !it.isDeleted && it.isFavorite })
    }

    override fun getTrashItems(): Flow<List<VaultItem>> {
        return vaultDao?.getTrashVaultItems()?.map { list ->
            list.map { it.toDomainModel() }
        } ?: flowOf(mockVaultItems.filter { it.isDeleted })
    }

    override fun getItemById(id: String): Flow<VaultItem?> {
        return vaultDao?.getVaultItemById(id)?.map { it?.toDomainModel() }
            ?: flowOf(mockVaultItems.find { it.id == id })
    }

    override fun searchItems(query: String): Flow<List<VaultItem>> {
        if (query.isBlank()) return getAllItems()
        return vaultDao?.searchVaultItems(query)?.map { list ->
            list.map { it.toDomainModel() }
        } ?: flowOf(mockVaultItems.filter { item ->
            !item.isDeleted && (
                item.title.contains(query, ignoreCase = true) ||
                item.subtitle.contains(query, ignoreCase = true) ||
                item.encryptedPayload.contains(query, ignoreCase = true) ||
                item.tags.any { it.contains(query, ignoreCase = true) } ||
                item.category.displayName.contains(query, ignoreCase = true)
            )
        })
    }

    override suspend fun saveItem(item: VaultItem) {
        val existingIndex = mockVaultItems.indexOfFirst { it.id == item.id }
        val updatedItem = if (existingIndex >= 0) {
            val existing = mockVaultItems[existingIndex]
            val newVerNumber = existing.versionNumber + 1
            val now = System.currentTimeMillis()
            // Append version record if title, subtitle or payload changed
            val newVersionRecord = VaultVersionRecord(
                versionNumber = newVerNumber,
                timestamp = now,
                titleSnapshot = item.title,
                subtitleSnapshot = item.subtitle,
                payloadSnapshot = item.encryptedPayload,
                changeNote = "Vault secret updated (v$newVerNumber)"
            )
            val newHistory = listOf(newVersionRecord) + existing.versionHistory
            item.copy(
                versionNumber = newVerNumber,
                modifiedTimestamp = now,
                lastModifiedAt = now,
                versionHistory = newHistory
            )
        } else {
            val now = System.currentTimeMillis()
            val initialVersion = VaultVersionRecord(
                versionNumber = item.versionNumber,
                timestamp = now,
                titleSnapshot = item.title,
                subtitleSnapshot = item.subtitle,
                payloadSnapshot = item.encryptedPayload,
                changeNote = "Initial Container Creation"
            )
            item.copy(
                versionHistory = listOf(initialVersion),
                createdAt = now,
                modifiedTimestamp = now,
                lastModifiedAt = now
            )
        }

        vaultDao?.insertVaultItem(updatedItem.toEntity()) ?: run {
            if (existingIndex >= 0) {
                mockVaultItems[existingIndex] = updatedItem
            } else {
                mockVaultItems.add(0, updatedItem)
            }
        }
    }

    override suspend fun toggleFavorite(id: String) {
        val item = mockVaultItems.find { it.id == id }
        if (item != null) {
            val updated = item.copy(isFavorite = !item.isFavorite)
            saveItem(updated)
        }
    }

    override suspend fun softDeleteItem(id: String) {
        val index = mockVaultItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            val softDeleted = mockVaultItems[index].copy(
                isDeleted = true,
                deletedAt = System.currentTimeMillis()
            )
            vaultDao?.softDeleteVaultItem(id, System.currentTimeMillis()) ?: run {
                mockVaultItems[index] = softDeleted
            }
        }
    }

    override suspend fun restoreItem(id: String) {
        val index = mockVaultItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            val restored = mockVaultItems[index].copy(
                isDeleted = false,
                deletedAt = null
            )
            vaultDao?.restoreVaultItem(id) ?: run {
                mockVaultItems[index] = restored
            }
        }
    }

    override suspend fun deleteItem(id: String) {
        vaultDao?.deleteVaultItemById(id) ?: run {
            mockVaultItems.removeAll { it.id == id }
        }
    }

    override suspend fun emptyTrash() {
        vaultDao?.emptyTrash() ?: run {
            mockVaultItems.removeAll { it.isDeleted }
        }
    }

    private fun VaultItemEntity.toDomainModel(): VaultItem {
        val tagsList = runCatching {
            val jsonArray = JSONArray(tagsJson)
            List(jsonArray.length()) { jsonArray.getString(it) }
        }.getOrDefault(emptyList())

        val historyList = runCatching {
            val jsonArray = JSONArray(versionHistoryJson)
            List(jsonArray.length()) { i ->
                val obj = jsonArray.getJSONObject(i)
                VaultVersionRecord(
                    versionNumber = obj.optInt("versionNumber", 1),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    titleSnapshot = obj.optString("titleSnapshot", ""),
                    subtitleSnapshot = obj.optString("subtitleSnapshot", ""),
                    payloadSnapshot = obj.optString("payloadSnapshot", ""),
                    changeNote = obj.optString("changeNote", "Revision")
                )
            }
        }.getOrDefault(emptyList())

        val matchedCategory = VaultCategory.values().find { it.name == categoryName } ?: VaultCategory.LOGIN

        return VaultItem(
            id = id,
            title = title,
            subtitle = subtitle,
            category = matchedCategory,
            encryptedPayload = encryptedPayload,
            iv = iv,
            salt = salt,
            isFavorite = isFavorite,
            tags = tagsList,
            isDeleted = isDeleted,
            deletedAt = deletedAt,
            versionHistory = historyList,
            securityGrade = runCatching { SecurityGrade.valueOf(securityGrade) }.getOrDefault(SecurityGrade.STRONG),
            syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.OFFLINE_ONLY),
            createdAt = createdAt,
            lastModifiedAt = lastModifiedAt,
            versionNumber = versionNumber,
            modifiedTimestamp = modifiedTimestamp,
            deviceId = deviceId,
            isConflictCopy = isConflictCopy,
            originalItemId = originalItemId,
            conflictReason = conflictReason,
            hasUnresolvedConflict = hasUnresolvedConflict
        )
    }

    private fun VaultItem.toEntity(): VaultItemEntity {
        val tagsArray = JSONArray(tags)
        val historyArray = JSONArray()
        versionHistory.forEach { rec ->
            val obj = JSONObject().apply {
                put("versionNumber", rec.versionNumber)
                put("timestamp", rec.timestamp)
                put("titleSnapshot", rec.titleSnapshot)
                put("subtitleSnapshot", rec.subtitleSnapshot)
                put("payloadSnapshot", rec.payloadSnapshot)
                put("changeNote", rec.changeNote)
            }
            historyArray.put(obj)
        }

        return VaultItemEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            categoryName = category.name,
            encryptedPayload = encryptedPayload,
            iv = iv,
            salt = salt,
            isFavorite = isFavorite,
            tagsJson = tagsArray.toString(),
            isDeleted = isDeleted,
            deletedAt = deletedAt,
            versionHistoryJson = historyArray.toString(),
            securityGrade = securityGrade.name,
            syncStatus = syncStatus.name,
            createdAt = createdAt,
            lastModifiedAt = lastModifiedAt,
            versionNumber = versionNumber,
            modifiedTimestamp = modifiedTimestamp,
            deviceId = deviceId,
            isConflictCopy = isConflictCopy,
            originalItemId = originalItemId,
            conflictReason = conflictReason,
            hasUnresolvedConflict = hasUnresolvedConflict
        )
    }
}

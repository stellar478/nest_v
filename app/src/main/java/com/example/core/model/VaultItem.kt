package com.example.core.model

import java.util.UUID

enum class VaultCategory(val displayName: String, val iconName: String) {
    ALL("All Containers", "Folder"),
    EMAILS("Emails & Mailboxes", "Email"),
    BANKING("Banking & Accounts", "AccountBalance"),
    CARDS("Payment Cards", "CreditCard"),
    SOCIAL_MEDIA("Social Media", "Share"),
    GAMING("Gaming Accounts", "SportsEsports"),
    APPS("App Credentials", "Apps"),
    DOCUMENTS("Encrypted Documents", "Description"),
    RECOVERY_CODES("Recovery Codes", "VpnKey"),
    NOTES("Secure Notes", "Notes"),
    CONTACTS("Encrypted Contacts", "Contacts"),
    // Backward compatibility aliases
    LOGIN("Logins & Passwords", "Key"),
    SECURE_NOTE("Secure Notes", "Description"),
    CARD("Payment Cards", "CreditCard"),
    DOCUMENT("Encrypted Docs", "Article"),
    IDENTITY("Identities & Passports", "Badge"),
    ARCHIVE("Archive", "Archive")
}

enum class SecurityGrade(val label: String) {
    STRONG("Strong"),
    MODERATE("Moderate"),
    WEAK("Weak"),
    COMPROMISED("Compromised")
}

enum class SyncStatus(val label: String) {
    OFFLINE_ONLY("Offline Vault"),
    SYNCED("Encrypted Sync"),
    PENDING("Pending Sync")
}

data class VaultVersionRecord(
    val versionNumber: Int,
    val timestamp: Long,
    val titleSnapshot: String,
    val subtitleSnapshot: String,
    val payloadSnapshot: String,
    val changeNote: String = "Vault Secret Revision"
)

data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String = "",
    val category: VaultCategory,
    val encryptedPayload: String,
    val iv: String = "nest_iv_" + UUID.randomUUID().toString().take(6),
    val salt: String = "nest_salt_" + UUID.randomUUID().toString().take(6),
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val versionHistory: List<VaultVersionRecord> = emptyList(),
    val securityGrade: SecurityGrade = SecurityGrade.STRONG,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE_ONLY,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val versionNumber: Int = 1,
    val modifiedTimestamp: Long = System.currentTimeMillis(),
    val deviceId: String = "device_nordic_pixel_8",
    val isConflictCopy: Boolean = false,
    val originalItemId: String? = null,
    val conflictReason: String? = null,
    val hasUnresolvedConflict: Boolean = false
)

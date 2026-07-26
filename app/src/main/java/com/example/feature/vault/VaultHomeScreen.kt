package com.example.feature.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestEmptyState
import com.example.core.designsystem.NestLockPromptDialog
import com.example.core.designsystem.NestSearchInput
import com.example.core.designsystem.NestSecurityBadge
import com.example.core.designsystem.NestSyncBadge
import com.example.core.designsystem.NestSkeletonList
import com.example.core.designsystem.NestTopBar
import com.example.core.model.VaultCategory
import com.example.core.model.VaultItem
import com.example.ui.theme.LocalNestColors

import com.example.feature.sync.ConflictResolutionDialog
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.foundation.layout.IntrinsicSize

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaultHomeScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToAudit: () -> Unit = {},
    viewModel: VaultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalNestColors.current
    var showUnlockDialog by remember { mutableStateOf(false) }

    if (showUnlockDialog || uiState.isVaultLocked) {
        NestLockPromptDialog(
            onUnlockWithPin = { pin ->
                if (pin == "1234") {
                    showUnlockDialog = false
                    if (uiState.isVaultLocked) viewModel.toggleVaultLock()
                }
            },
            onUnlockWithBiometric = {
                showUnlockDialog = false
                if (uiState.isVaultLocked) viewModel.toggleVaultLock()
            },
            onDismiss = {
                showUnlockDialog = false
            }
        )
    }

    // Active Conflict Resolution Dialog
    val activeLocal = uiState.activeConflictLocalItem
    val activeConflict = uiState.activeConflictCopyItem
    if (activeLocal != null && activeConflict != null) {
        ConflictResolutionDialog(
            localItem = activeLocal,
            conflictItem = activeConflict,
            onKeepLocal = { viewModel.resolveKeepLocal(activeLocal.id, activeConflict.id) },
            onKeepRemote = { viewModel.resolveKeepRemote(activeLocal.id, activeConflict.id) },
            onMerge = { mergedTitle, mergedSub, mergedPayload ->
                viewModel.resolveMerge(activeLocal.id, activeConflict.id, mergedTitle, mergedSub, mergedPayload)
            },
            onDismiss = { viewModel.closeConflictResolver() }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NestTopBar(
                title = "Nest Vault",
                subtitle = "AES-256 Hardware Encrypted Containers",
                isVaultLocked = uiState.isVaultLocked,
                onLockToggleClick = { showUnlockDialog = true }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dashboard Hero Bento Grid
                item {
                    BentoGridHeader(
                        totalEntriesCount = uiState.totalEncryptedCount,
                        securityScore = uiState.securityScore,
                        vaultHealthStatus = uiState.vaultHealthStatus,
                        lastBackupTime = uiState.lastBackupTime,
                        trustedDevicesCount = uiState.trustedDevicesCount,
                        trashCount = uiState.trashItems.size,
                        isShowingTrash = uiState.isShowingTrash,
                        onQuickAddClick = onNavigateToCreate,
                        onQuickAuditClick = onNavigateToAudit,
                        onTriggerBackupClick = { viewModel.triggerBackup() },
                        onToggleTrashClick = { viewModel.toggleShowingTrash() },
                        onLockVaultClick = { showUnlockDialog = true }
                    )
                }

                // Offline Sync Control & Conflict Banner
                item {
                    NestCard(
                        cornerRadius = 20.dp,
                        padding = 16.dp,
                        backgroundColor = if (uiState.totalConflictsCount > 0) colors.warning.copy(alpha = 0.12f) else colors.card
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (uiState.totalConflictsCount > 0) colors.warning.copy(alpha = 0.2f)
                                                else colors.primaryAccent.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.totalConflictsCount > 0) Icons.Default.Warning else Icons.Default.Sync,
                                            contentDescription = null,
                                            tint = if (uiState.totalConflictsCount > 0) colors.warning else colors.primaryAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = if (uiState.totalConflictsCount > 0) "⚡ Conflict Copy Detected!" else "Offline Sync Engine",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primaryText
                                        )
                                        Text(
                                            text = if (uiState.totalConflictsCount > 0) "${uiState.totalConflictsCount} conflict copy saved (No data lost)" else "Syncs encrypted ciphertexts only",
                                            fontSize = 11.sp,
                                            color = colors.secondaryText
                                        )
                                    }
                                }

                                Row {
                                    NestButton(
                                        text = if (uiState.isSyncing) "Syncing..." else "Sync Now",
                                        onClick = { viewModel.triggerOfflineSync() },
                                        variant = NestButtonVariant.PRIMARY,
                                        enabled = !uiState.isSyncing,
                                        testTag = "trigger_offline_sync_btn"
                                    )
                                }
                            }

                            if (uiState.syncLogMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.syncLogMessage ?: "",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = colors.primaryAccent
                                )
                            }
                        }
                    }
                }

                // Search Bar
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    NestSearchInput(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        placeholder = if (uiState.isShowingTrash) "Search trash items..." else "Search emails, banking, cards, social, keys, tags...",
                        testTag = "vault_search_input"
                    )
                }

                // Container / Category Pills (10 Containers + All)
                if (!uiState.isShowingTrash) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Container Modules",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(VaultCategory.values()) { category ->
                                    val isSelected = uiState.selectedCategory == category
                                    val shape = RoundedCornerShape(20.dp)

                                    Box(
                                        modifier = Modifier
                                            .testTag("category_pill_${category.name.lowercase()}")
                                            .clip(shape)
                                            .background(if (isSelected) colors.primaryAccent else colors.card)
                                            .clickable { viewModel.selectCategory(category) }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = category.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) colors.card else colors.primaryText
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trash Bin (${uiState.trashItems.size} Items)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.warning
                            )

                            if (uiState.trashItems.isNotEmpty()) {
                                NestButton(
                                    text = "Empty Trash",
                                    onClick = { viewModel.emptyTrash() },
                                    variant = NestButtonVariant.GHOST,
                                    testTag = "empty_trash_button"
                                )
                            }
                        }
                    }
                }

                // Section Header: Recent Entries or Trash List
                item {
                    Text(
                        text = if (uiState.isShowingTrash) "Soft Deleted Items" else "${uiState.selectedCategory.displayName} (${if (uiState.isShowingTrash) uiState.trashItems.size else uiState.items.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                val displayedItems = if (uiState.isShowingTrash) uiState.trashItems else uiState.items

                if (uiState.isLoading) {
                    item {
                        NestSkeletonList(
                            count = 4,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                } else if (displayedItems.isEmpty()) {
                    item {
                        NestEmptyState(
                            title = if (uiState.isShowingTrash) "Trash Bin is Empty" else "No Items in ${uiState.selectedCategory.displayName}",
                            description = if (uiState.isShowingTrash) "Items soft deleted from containers will appear here for recovery." else "Your zero-knowledge container is empty. Create a new encrypted secret entry.",
                            actionButtonText = if (uiState.isShowingTrash) "Exit Trash" else "Create Container Entry",
                            onActionClick = {
                                if (uiState.isShowingTrash) viewModel.toggleShowingTrash() else onNavigateToCreate()
                            },
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(displayedItems, key = { it.id }) { item ->
                        if (uiState.isShowingTrash) {
                            TrashItemRowCard(
                                item = item,
                                onRestore = { viewModel.restoreFromTrash(item.id) }
                            )
                        } else {
                            VaultItemRowCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                                onResolveConflict = { viewModel.openConflictResolver(item) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToCreate,
            containerColor = colors.primaryAccent,
            contentColor = colors.card,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("vault_add_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vault Entry")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VaultItemRowCard(
    item: VaultItem,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onResolveConflict: () -> Unit = {}
) {
    val colors = LocalNestColors.current

    val categoryIcon = when (item.category) {
        VaultCategory.EMAILS -> Icons.Default.AlternateEmail
        VaultCategory.BANKING -> Icons.Default.AccountBalance
        VaultCategory.CARDS -> Icons.Default.CreditCard
        VaultCategory.SOCIAL_MEDIA -> Icons.Default.Share
        VaultCategory.GAMING -> Icons.Default.SportsEsports
        VaultCategory.APPS -> Icons.Default.Apps
        VaultCategory.DOCUMENTS -> Icons.Default.Description
        VaultCategory.RECOVERY_CODES -> Icons.Default.VpnKey
        VaultCategory.NOTES -> Icons.Default.Notes
        VaultCategory.CONTACTS -> Icons.Default.Contacts
        VaultCategory.LOGIN -> Icons.Default.Key
        else -> Icons.Default.Folder
    }

    NestCard(
        onClick = onClick,
        cornerRadius = 24.dp,
        padding = 16.dp,
        backgroundColor = if (item.hasUnresolvedConflict || item.isConflictCopy) colors.warning.copy(alpha = 0.08f) else colors.card,
        testTag = "vault_item_card_${item.id}"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.hasUnresolvedConflict || item.isConflictCopy) colors.warning.copy(alpha = 0.2f)
                            else colors.secondaryBackground
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.hasUnresolvedConflict || item.isConflictCopy) Icons.Default.Warning else categoryIcon,
                        contentDescription = null,
                        tint = if (item.hasUnresolvedConflict || item.isConflictCopy) colors.warning else colors.primaryAccent
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (item.isFavorite) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                tint = colors.error,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (item.subtitle.isNotEmpty()) {
                        Text(
                            text = item.subtitle,
                            fontSize = 13.sp,
                            color = colors.secondaryText
                        )
                    }

                    // Metadata row: Version Number, Device ID
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "v${item.versionNumber} • Device: ${item.deviceId}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.secondaryText
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    NestSecurityBadge(grade = item.securityGrade)
                    Spacer(modifier = Modifier.height(4.dp))
                    NestSyncBadge(syncStatus = item.syncStatus)
                }
            }

            // Conflict warning & Resolve button
            if (item.hasUnresolvedConflict || item.isConflictCopy) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.warning.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (item.isConflictCopy) "⚡ Conflict Copy" else "⚠️ Conflict Exists",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.warning
                    )

                    NestButton(
                        text = "Resolve / Merge",
                        onClick = onResolveConflict,
                        variant = NestButtonVariant.PRIMARY,
                        icon = Icons.Default.CallMerge,
                        testTag = "resolve_conflict_button_${item.id}"
                    )
                }
            }

            // Tags row preview
            if (item.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.secondaryBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    tint = colors.primaryAccent,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    color = colors.secondaryText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrashItemRowCard(
    item: VaultItem,
    onRestore: () -> Unit
) {
    val colors = LocalNestColors.current

    NestCard(
        cornerRadius = 24.dp,
        padding = 16.dp,
        testTag = "trash_item_card_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.warning.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = colors.warning
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText
                )
                Text(
                    text = "Soft Deleted (${item.category.displayName})",
                    fontSize = 12.sp,
                    color = colors.secondaryText
                )
            }

            NestButton(
                text = "Restore",
                onClick = onRestore,
                variant = NestButtonVariant.OUTLINE,
                icon = Icons.Default.Restore,
                testTag = "restore_item_button_${item.id}"
            )
        }
    }
}

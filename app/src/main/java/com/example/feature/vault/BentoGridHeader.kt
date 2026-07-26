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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.ui.theme.LocalNestColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BentoGridHeader(
    totalEntriesCount: Int = 177,
    securityScore: Int = 98,
    vaultHealthStatus: String = "Optimal Hardware Key Protection",
    lastBackupTime: String = "Today, 08:30 AM",
    trustedDevicesCount: Int = 3,
    trashCount: Int = 0,
    isShowingTrash: Boolean = false,
    onQuickAddClick: () -> Unit = {},
    onQuickAuditClick: () -> Unit = {},
    onTriggerBackupClick: () -> Unit = {},
    onToggleTrashClick: () -> Unit = {},
    onLockVaultClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalNestColors.current
    var isOfflineModeActive by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card: Vault Health & Security Score
        NestCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 32.dp,
            padding = 20.dp,
            testTag = "bento_hero_security_card"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.success.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = colors.success,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "SECURITY SCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$securityScore",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                            Text(
                                text = "/100",
                                fontSize = 14.sp,
                                color = colors.secondaryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Vault Alpha",
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    color = colors.primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$vaultHealthStatus • AES-256 GCM",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.secondaryText
                )
            }
        }

        // Metrics Grid 3x Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Total Entries
            NestCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 24.dp,
                padding = 14.dp,
                testTag = "bento_total_entries_card"
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.secondaryBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = colors.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$totalEntriesCount",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = colors.primaryText
                    )
                    Text(
                        text = "TOTAL ENTRIES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.secondaryText
                    )
                }
            }

            // Card 2: Trusted Devices
            NestCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 24.dp,
                padding = 14.dp,
                testTag = "bento_trusted_devices_card"
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.secondaryBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhonelinkSetup,
                            contentDescription = null,
                            tint = colors.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$trustedDevicesCount",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = colors.primaryText
                    )
                    Text(
                        text = "DEVICES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.secondaryText
                    )
                }
            }

            // Card 3: Trash Bin
            NestCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggleTrashClick() },
                backgroundColor = if (isShowingTrash) colors.primaryAccent.copy(alpha = 0.15f) else colors.card,
                cornerRadius = 24.dp,
                padding = 14.dp,
                testTag = "bento_trash_card"
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.secondaryBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (trashCount > 0) colors.warning else colors.secondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$trashCount",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = colors.primaryText
                    )
                    Text(
                        text = if (isShowingTrash) "VIEWING TRASH" else "TRASH BIN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isShowingTrash) colors.primaryAccent else colors.secondaryText
                    )
                }
            }
        }

        // Backups & Quick Actions Card
        NestCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = colors.secondaryBackground,
            cornerRadius = 28.dp,
            padding = 16.dp,
            testTag = "bento_backups_card"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.card),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = colors.primaryAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zero-Knowledge Backups",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                        Text(
                            text = "Last Backup: $lastBackupTime",
                            fontSize = 12.sp,
                            color = colors.secondaryText
                        )
                    }

                    NestButton(
                        text = "Backup Now",
                        onClick = onTriggerBackupClick,
                        variant = NestButtonVariant.OUTLINE,
                        testTag = "bento_backup_now_button"
                    )
                }

                // Quick Action Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionChip(
                        label = "New Entry",
                        icon = Icons.Default.Add,
                        onClick = onQuickAddClick,
                        testTag = "quick_action_add"
                    )
                    QuickActionChip(
                        label = "Audit Vault",
                        icon = Icons.Default.Security,
                        onClick = onQuickAuditClick,
                        testTag = "quick_action_audit"
                    )
                    QuickActionChip(
                        label = if (isShowingTrash) "Exit Trash" else "Trash ($trashCount)",
                        icon = Icons.Default.Delete,
                        onClick = onToggleTrashClick,
                        testTag = "quick_action_trash"
                    )
                    QuickActionChip(
                        label = "Lock Vault",
                        icon = Icons.Default.Lock,
                        onClick = onLockVaultClick,
                        testTag = "quick_action_lock"
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    val colors = LocalNestColors.current
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primaryAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText
            )
        }
    }
}

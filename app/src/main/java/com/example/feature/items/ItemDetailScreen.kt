package com.example.feature.items

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestSecurityBadge
import com.example.core.designsystem.NestSyncBadge
import com.example.core.designsystem.NestSkeletonList
import com.example.core.designsystem.NestTopBar
import com.example.core.model.VaultVersionRecord
import com.example.ui.theme.LocalNestColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    viewModel: ItemViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalNestColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    LaunchedEffect(uiState.isDeletedSuccess) {
        if (uiState.isDeletedSuccess) {
            Toast.makeText(context, "Item moved to Trash Bin", Toast.LENGTH_SHORT).show()
            onDeleteClick(itemId)
        }
    }

    val item = uiState.item

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        NestTopBar(
            title = item?.title ?: "Encrypted Detail",
            subtitle = item?.category?.displayName ?: "Vault Container",
            onBackClick = onBackClick,
            actions = {
                IconButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier.testTag("detail_favorite_button")
                ) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (uiState.isFavorite) colors.error else colors.secondaryText
                    )
                }
                IconButton(
                    onClick = { onEditClick(itemId) },
                    modifier = Modifier.testTag("detail_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit entry",
                        tint = colors.primaryAccent
                    )
                }
                IconButton(
                    onClick = { viewModel.softDeleteItem(itemId) },
                    modifier = Modifier.testTag("detail_delete_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = colors.error
                    )
                }
            }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                NestSkeletonList(count = 3)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Header Info Card
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.title.ifEmpty { "Untitled Secret" },
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                            if (uiState.subtitle.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.subtitle,
                                    fontSize = 14.sp,
                                    color = colors.secondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item?.let {
                            NestSecurityBadge(grade = it.securityGrade)
                            NestSyncBadge(syncStatus = it.syncStatus)
                        }
                    }

                    // Tags Section
                    if (uiState.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "TAGS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.secondaryBackground)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Tag,
                                            contentDescription = null,
                                            tint = colors.primaryAccent,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = tag,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primaryText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Encrypted Payload Card
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Encrypted Secret Payload (AES-256-GCM)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (uiState.isPayloadVisible) uiState.payload else "••••••••••••••••••••••••••••••••",
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.primaryText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.secondaryBackground)
                            .padding(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        NestButton(
                            text = if (uiState.isPayloadVisible) "Hide Secret" else "Reveal Secret",
                            onClick = { viewModel.togglePayloadVisibility() },
                            variant = NestButtonVariant.OUTLINE,
                            modifier = Modifier.weight(1f),
                            testTag = "detail_reveal_button"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        NestButton(
                            text = "Copy Payload",
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Payload", uiState.payload)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Encrypted payload copied! Clipboard will auto-clear in 30s.", Toast.LENGTH_SHORT).show()
                            },
                            variant = NestButtonVariant.PRIMARY,
                            icon = Icons.Default.ContentCopy,
                            modifier = Modifier.weight(1f),
                            testTag = "detail_copy_button"
                        )
                    }
                }
            }

            // Version History Module
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.primaryAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = colors.primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Version History (${uiState.versionHistory.size} Revisions)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.versionHistory.isEmpty()) {
                        Text(
                            text = "Initial revision active. Future edits will create encrypted snapshots.",
                            fontSize = 13.sp,
                            color = colors.secondaryText
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.versionHistory.forEach { version ->
                                VersionHistoryRow(
                                    version = version,
                                    onRestoreClick = {
                                        viewModel.restoreVersion(version)
                                        Toast.makeText(context, "Restored snapshot v${version.versionNumber}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Security Metadata Card
            NestCard(
                cornerRadius = 24.dp,
                padding = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Metadata & Offline Sync Diagnostics",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version Number: v${item?.versionNumber ?: 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryAccent
                    )
                    Text(
                        text = "Modified Timestamp: ${formatDate(item?.modifiedTimestamp ?: item?.lastModifiedAt)}",
                        fontSize = 11.sp,
                        color = colors.secondaryText
                    )
                    Text(
                        text = "Device ID: ${item?.deviceId ?: "device_nordic_pixel_8"}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.secondaryText
                    )
                    Text(
                        text = "Sync Status: ${item?.syncStatus?.label ?: "Offline Vault"}",
                        fontSize = 11.sp,
                        color = colors.secondaryText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "IV: ${item?.iv ?: "nest_iv_default"}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.secondaryText
                    )
                    Text(
                        text = "Salt: ${item?.salt ?: "nest_salt_default"}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.secondaryText
                    )
                }
            }
        }
    }
}
}

@Composable
private fun VersionHistoryRow(
    version: VaultVersionRecord,
    onRestoreClick: () -> Unit
) {
    val colors = LocalNestColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.secondaryBackground)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "v${version.versionNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatDate(version.timestamp),
                        fontSize = 11.sp,
                        color = colors.secondaryText
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = version.changeNote,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText
                )
                Text(
                    text = "Title: ${version.titleSnapshot}",
                    fontSize = 11.sp,
                    color = colors.secondaryText
                )
            }

            NestButton(
                text = "Revert",
                onClick = onRestoreClick,
                variant = NestButtonVariant.OUTLINE,
                icon = Icons.Default.Restore,
                testTag = "revert_version_v${version.versionNumber}"
            )
        }
    }
}

private fun formatDate(timeMs: Long?): String {
    if (timeMs == null || timeMs == 0L) return "N/A"
    return SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(timeMs))
}

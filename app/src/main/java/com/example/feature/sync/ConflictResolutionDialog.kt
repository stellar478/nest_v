package com.example.feature.sync

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestTextField
import com.example.core.model.VaultItem
import com.example.ui.theme.LocalNestColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConflictResolutionDialog(
    localItem: VaultItem,
    conflictItem: VaultItem,
    onKeepLocal: () -> Unit,
    onKeepRemote: () -> Unit,
    onMerge: (mergedTitle: String, mergedSubtitle: String, mergedPayload: String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalNestColors.current
    val context = LocalContext.current
    var isMergeModeActive by remember { mutableStateOf(false) }

    var mergedTitleInput by remember { mutableStateOf(localItem.title) }
    var mergedSubtitleInput by remember { mutableStateOf(localItem.subtitle) }
    var mergedPayloadInput by remember { mutableStateOf("${localItem.encryptedPayload} | MERGED") }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }
    val shape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .testTag("conflict_resolution_dialog")
                .fillMaxWidth()
                .clip(shape)
                .background(colors.card)
                .border(1.dp, colors.border, shape)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "⚡ Offline Conflict Resolution",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )

            Text(
                text = conflictItem.conflictReason ?: "Concurrent modifications detected across devices. Data was preserved in a conflict copy.",
                fontSize = 12.sp,
                color = colors.secondaryText
            )

            if (!isMergeModeActive) {
                // Side-by-side or stacked version comparison
                Text(
                    text = "Compare entries before resolving:",
                    fontSize = 12.sp,
                    color = colors.secondaryText,
                    fontWeight = FontWeight.Bold
                )

                // 1. Local Version Card
                NestCard(
                    cornerRadius = 20.dp,
                    padding = 16.dp,
                    backgroundColor = colors.secondaryBackground
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colors.primaryAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = colors.primaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Local Version",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.primaryAccent)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "v${localItem.versionNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.card
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = localItem.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                        if (localItem.subtitle.isNotBlank()) {
                            Text(
                                text = localItem.subtitle,
                                fontSize = 12.sp,
                                color = colors.secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Device: ${localItem.deviceId}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.secondaryText
                            )
                            Text(
                                text = dateFormat.format(Date(localItem.modifiedTimestamp)),
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.card)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Cipher: ${localItem.encryptedPayload}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.primaryText,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        NestButton(
                            text = "Keep Local Version",
                            onClick = {
                                onKeepLocal()
                                Toast.makeText(context, "Conflict resolved. Kept local version.", Toast.LENGTH_SHORT).show()
                            },
                            variant = NestButtonVariant.OUTLINE,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "keep_local_version_btn"
                        )
                    }
                }

                // 2. Conflict Copy / Remote Version Card
                NestCard(
                    cornerRadius = 20.dp,
                    padding = 16.dp,
                    backgroundColor = colors.warning.copy(alpha = 0.08f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colors.warning.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = colors.warning,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Conflict / Remote Version",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.warning)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "v${conflictItem.versionNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.card
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = conflictItem.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                        if (conflictItem.subtitle.isNotBlank()) {
                            Text(
                                text = conflictItem.subtitle,
                                fontSize = 12.sp,
                                color = colors.secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Device: ${conflictItem.deviceId}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.secondaryText
                            )
                            Text(
                                text = dateFormat.format(Date(conflictItem.modifiedTimestamp)),
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.card)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Cipher: ${conflictItem.encryptedPayload}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.primaryText,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        NestButton(
                            text = "Keep Remote / Conflict Version",
                            onClick = {
                                onKeepRemote()
                                Toast.makeText(context, "Conflict resolved. Replaced with remote version.", Toast.LENGTH_SHORT).show()
                            },
                            variant = NestButtonVariant.OUTLINE,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "keep_remote_version_btn"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NestButton(
                        text = "Merge Versions",
                        onClick = { isMergeModeActive = true },
                        variant = NestButtonVariant.PRIMARY,
                        icon = Icons.Default.CallMerge,
                        modifier = Modifier.weight(1f),
                        testTag = "activate_merge_mode_btn"
                    )

                    NestButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        variant = NestButtonVariant.GHOST,
                        modifier = Modifier.weight(0.8f),
                        testTag = "cancel_conflict_dialog_btn"
                    )
                }
            } else {
                // Merge Mode Inline Form
                Text(
                    text = "Combine local & remote fields into a unified v${maxOf(localItem.versionNumber, conflictItem.versionNumber) + 1} entry:",
                    fontSize = 12.sp,
                    color = colors.secondaryText
                )

                NestTextField(
                    value = mergedTitleInput,
                    onValueChange = { mergedTitleInput = it },
                    label = "Merged Title",
                    testTag = "merged_title_input"
                )

                NestTextField(
                    value = mergedSubtitleInput,
                    onValueChange = { mergedSubtitleInput = it },
                    label = "Merged Subtitle / Account",
                    testTag = "merged_subtitle_input"
                )

                NestTextField(
                    value = mergedPayloadInput,
                    onValueChange = { mergedPayloadInput = it },
                    label = "Merged Encrypted Payload (AES-256)",
                    testTag = "merged_payload_input"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NestButton(
                        text = "Save Merged Entry",
                        onClick = {
                            onMerge(mergedTitleInput, mergedSubtitleInput, mergedPayloadInput)
                            Toast.makeText(context, "Conflict resolved! Merged entry created.", Toast.LENGTH_SHORT).show()
                        },
                        variant = NestButtonVariant.PRIMARY,
                        modifier = Modifier.weight(1f),
                        testTag = "save_merged_entry_btn"
                    )

                    NestButton(
                        text = "Back",
                        onClick = { isMergeModeActive = false },
                        variant = NestButtonVariant.OUTLINE,
                        modifier = Modifier.weight(0.8f),
                        testTag = "back_from_merge_btn"
                    )
                }
            }
        }
    }
}

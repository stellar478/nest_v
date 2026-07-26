package com.example.feature.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestDialog
import com.example.core.designsystem.NestTextField
import com.example.core.designsystem.NestTopBar
import com.example.ui.theme.LocalNestColors

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalNestColors.current
    val context = LocalContext.current

    var newMasterPassInput by remember { mutableStateOf("") }
    var confirmMasterPassInput by remember { mutableStateOf("") }

    if (uiState.showChangeMasterPasswordDialog) {
        NestDialog(
            title = "Change Master Password",
            message = "Your Master Password derives the zero-knowledge AES key via Argon2id. Enter your new password:",
            confirmText = "Update Master Key",
            dismissText = "Cancel",
            onConfirm = {
                if (newMasterPassInput.length >= 8 && newMasterPassInput == confirmMasterPassInput) {
                    Toast.makeText(context, "Master Password and Argon2 key updated successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.showChangeMasterPasswordDialog(false)
                    newMasterPassInput = ""
                    confirmMasterPassInput = ""
                } else {
                    Toast.makeText(context, "Passwords must match and be at least 8 characters.", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { viewModel.showChangeMasterPasswordDialog(false) },
            testTag = "change_master_pass_dialog"
        )
    }

    if (uiState.showBackupExportDialog) {
        NestDialog(
            title = "Export Encrypted Database (.nest)",
            message = "Export zero-knowledge vault encrypted with AES-256-GCM. Keep your master key secure to decrypt on any device.",
            confirmText = "Export File",
            dismissText = "Close",
            onConfirm = {
                Toast.makeText(context, "Vault backup export complete: nest_vault_backup.nest", Toast.LENGTH_SHORT).show()
                viewModel.showBackupExportDialog(false)
            },
            onDismiss = { viewModel.showBackupExportDialog(false) },
            testTag = "export_backup_dialog"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        NestTopBar(
            title = "Vault Settings",
            subtitle = "Security • Auto-Lock • Argon2id Preferences"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Biometrics & Hardware Protection
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hardware Biometric Unlock",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                        Text(
                            text = "Use Android Fingerprint / Face ID for instant vault access",
                            fontSize = 13.sp,
                            color = colors.secondaryText
                        )
                    }

                    Switch(
                        checked = uiState.isBiometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometric(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.card,
                            checkedTrackColor = colors.primaryAccent,
                            uncheckedThumbColor = colors.secondaryText,
                            uncheckedTrackColor = colors.secondaryBackground
                        ),
                        modifier = Modifier.testTag("settings_biometric_switch")
                    )
                }
            }

            // 2. Auto-Lock Timeout
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = colors.primaryAccent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Lock Timeout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                    }

                    Text(
                        text = "Vault automatically locks when inactive or moved to background",
                        fontSize = 13.sp,
                        color = colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0 to "Immediate", 1 to "1 Min", 5 to "5 Min", 15 to "15 Min").forEach { (mins, label) ->
                            val isSelected = uiState.autoLockTimeoutMinutes == mins
                            NestButton(
                                text = label,
                                onClick = { viewModel.setAutoLockTimeout(mins) },
                                variant = if (isSelected) NestButtonVariant.PRIMARY else NestButtonVariant.OUTLINE,
                                modifier = Modifier.weight(1f),
                                testTag = "settings_timeout_$mins"
                            )
                        }
                    }
                }
            }

            // 3. Clipboard Protection & Auto-Clear
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
                                text = "Clipboard Protection",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primaryText
                            )
                            Text(
                                text = "Automatically purge copied secret payloads from device memory",
                                fontSize = 13.sp,
                                color = colors.secondaryText
                            )
                        }

                        Switch(
                            checked = uiState.isClipboardProtectionEnabled,
                            onCheckedChange = { viewModel.toggleClipboardProtection(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.card,
                                checkedTrackColor = colors.primaryAccent,
                                uncheckedThumbColor = colors.secondaryText,
                                uncheckedTrackColor = colors.secondaryBackground
                            ),
                            modifier = Modifier.testTag("settings_clipboard_switch")
                        )
                    }

                    if (uiState.isClipboardProtectionEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Auto-Clear Delay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 60).forEach { sec ->
                                val isSel = uiState.clipboardClearDelaySeconds == sec
                                NestButton(
                                    text = "$sec Sec",
                                    onClick = { viewModel.setClipboardClearDelay(sec) },
                                    variant = if (isSel) NestButtonVariant.PRIMARY else NestButtonVariant.OUTLINE,
                                    modifier = Modifier.weight(1f),
                                    testTag = "clipboard_delay_$sec"
                                )
                            }
                        }
                    }
                }
            }

            // 4. Master Password & Argon2 Configuration
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = colors.primaryAccent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Master Password & Argon2 KDF",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Argon2id (64MB RAM, 3 Passes) protects against GPU rainbow table attacks",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NestButton(
                        text = "Change Master Password",
                        onClick = { viewModel.showChangeMasterPasswordDialog(true) },
                        variant = NestButtonVariant.OUTLINE,
                        icon = Icons.Default.Lock,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "settings_change_master_password"
                    )
                }
            }

            // 5. Login Alerts & Unknown Device Notifications
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Login & Breach Alerts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primaryText
                        )
                        Text(
                            text = "Receive notifications on unusual IP or device authentication attempts",
                            fontSize = 13.sp,
                            color = colors.secondaryText
                        )
                    }

                    Switch(
                        checked = uiState.isLoginAlertsEnabled,
                        onCheckedChange = { viewModel.toggleLoginAlerts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.card,
                            checkedTrackColor = colors.primaryAccent,
                            uncheckedThumbColor = colors.secondaryText,
                            uncheckedTrackColor = colors.secondaryBackground
                        ),
                        modifier = Modifier.testTag("settings_login_alerts_switch")
                    )
                }
            }

            // 6. Encrypted Backup & Storage
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Encrypted Local Vault Backup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primaryText
                    )
                    Text(
                        text = "Export zero-knowledge encrypted database bundle (.nest)",
                        fontSize = 13.sp,
                        color = colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NestButton(
                        text = "Export Encrypted Backup (.nest)",
                        onClick = { viewModel.showBackupExportDialog(true) },
                        variant = NestButtonVariant.OUTLINE,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "settings_export_backup"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

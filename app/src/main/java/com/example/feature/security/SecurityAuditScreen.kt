package com.example.feature.security

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestColumn
import com.example.core.designsystem.NestTable
import com.example.core.designsystem.NestTopBar
import com.example.core.security.TrustedDevice
import com.example.ui.theme.LocalNestColors

@Composable
fun SecurityAuditScreen(
    viewModel: SecurityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalNestColors.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        NestTopBar(
            title = "Security Audit & Health",
            subtitle = "Argon2 KDF • AES-256-GCM • Health Check"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Vault Health & Security Score Header Card
            NestCard(
                cornerRadius = 32.dp,
                padding = 24.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(colors.primaryAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isHealthCheckRunning) {
                                CircularProgressIndicator(
                                    color = colors.primaryAccent,
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Text(
                                    text = "${uiState.overallSecurityScore}%",
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.overallSecurityScore >= 90) colors.success else colors.warning
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vault Health Score",
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.weakPasswordsCount == 0)
                                    "Zero Vulnerabilities • AES-256 Protected"
                                else
                                    "${uiState.weakPasswordsCount} Weak / Reused Passwords Found",
                                fontSize = 13.sp,
                                color = if (uiState.weakPasswordsCount == 0) colors.success else colors.warning,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (uiState.healthCheckMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.secondaryBackground)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = colors.primaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.healthCheckMessage!!,
                                    fontSize = 12.sp,
                                    color = colors.primaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NestButton(
                            text = "Run Health Check",
                            onClick = { viewModel.runVaultHealthCheck() },
                            variant = NestButtonVariant.OUTLINE,
                            icon = Icons.Default.Refresh,
                            modifier = Modifier.weight(1f),
                            testTag = "run_health_check_button"
                        )

                        if (uiState.weakPasswordsCount > 0) {
                            NestButton(
                                text = "Auto-Remediate All",
                                onClick = {
                                    viewModel.applyAutoFixRemediation()
                                    Toast.makeText(context, "All weak credentials upgraded to strong AES keys!", Toast.LENGTH_SHORT).show()
                                },
                                variant = NestButtonVariant.PRIMARY,
                                icon = Icons.Default.AutoFixHigh,
                                modifier = Modifier.weight(1f),
                                testTag = "auto_remediate_button"
                            )
                        }
                    }
                }
            }

            // 2. Cryptographic Protocol & Argon2id Specs Card
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = colors.primaryAccent
                        )
                        Text(
                            text = "Argon2id KDF & Hardware Cipher Specs",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CryptoSpecPill("KDF Type", uiState.argon2Params.type)
                        CryptoSpecPill("Memory", "${uiState.argon2Params.memoryMb} MB")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CryptoSpecPill("Time Iterations", "${uiState.argon2Params.iterations} passes")
                        CryptoSpecPill("Parallel Threads", "${uiState.argon2Params.parallelism} threads")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CryptoSpecPill("Salt Length", "${uiState.argon2Params.saltLengthBytes} bytes")
                        CryptoSpecPill("Hardware Keystore", if (uiState.isHardwareKeystoreActive) "Active (TEE / StrongBox)" else "Software Fallback")
                    }
                }
            }

            // 3. Login Alerts & Session Security
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = colors.warning
                        )
                        Text(
                            text = "Login Alerts & Session Logs (${uiState.loginAlerts.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.loginAlerts.isEmpty()) {
                        Text(
                            text = "No unusual authentication attempts recorded.",
                            fontSize = 13.sp,
                            color = colors.secondaryText
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.loginAlerts.forEach { alert ->
                                LoginAlertRow(
                                    alert = alert,
                                    onDismiss = { viewModel.dismissAlert(alert.id) }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Trusted Hardware Devices Management
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = colors.primaryAccent
                        )
                        Text(
                            text = "Trusted Devices (${uiState.trustedDevices.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.trustedDevices.forEach { device ->
                            TrustedDeviceRow(
                                device = device,
                                onRevoke = {
                                    viewModel.revokeTrustedDevice(device.id)
                                    Toast.makeText(context, "Revoked access for ${device.deviceName}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            // 5. Key Audit Records Table
            Text(
                text = "Detailed Vault Audit Records (${uiState.auditRows.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                modifier = Modifier.padding(start = 4.dp)
            )

            NestTable(
                headers = listOf("Asset", "Category", "Grade", "Status"),
                items = uiState.auditRows,
                columns = listOf(
                    NestColumn(
                        title = "Asset Name",
                        weight = 1.8f,
                        cellContent = { row ->
                            Text(
                                text = row.asset,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primaryText
                            )
                        }
                    ),
                    NestColumn(
                        title = "Category",
                        weight = 1.2f,
                        cellContent = { row ->
                            Text(
                                text = row.category,
                                fontSize = 12.sp,
                                color = colors.secondaryText
                            )
                        }
                    ),
                    NestColumn(
                        title = "Grade",
                        weight = 1f,
                        cellContent = { row ->
                            Text(
                                text = row.grade,
                                fontSize = 12.sp,
                                color = if (row.grade == "Weak") colors.warning else colors.success,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    ),
                    NestColumn(
                        title = "Status",
                        weight = 1.2f,
                        cellContent = { row ->
                            Text(
                                text = row.status,
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                        }
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CryptoSpecPill(label: String, value: String) {
    val colors = LocalNestColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.secondaryBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = colors.secondaryText,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                fontSize = 12.sp,
                color = colors.primaryText,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun LoginAlertRow(
    alert: LoginAlert,
    onDismiss: () -> Unit
) {
    val colors = LocalNestColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (alert.isFlagged) colors.warning.copy(alpha = 0.1f) else colors.secondaryBackground)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (alert.isSuccess) colors.success.copy(alpha = 0.15f) else colors.warning.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (alert.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (alert.isSuccess) colors.success else colors.warning,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.deviceName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
                Text(
                    text = "${alert.location} • ${alert.ipAddress}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = colors.secondaryText
                )
                Text(
                    text = alert.timestamp,
                    fontSize = 11.sp,
                    color = colors.secondaryText
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_alert_${alert.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = colors.secondaryText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TrustedDeviceRow(
    device: TrustedDevice,
    onRevoke: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.primaryAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    tint = colors.primaryAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.deviceName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )
                    if (device.isCurrentDevice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.primaryAccent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "THIS DEVICE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.card
                            )
                        }
                    }
                }
                Text(
                    text = "IP: ${device.ipAddress} • Active: ${device.lastActiveTime}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = colors.secondaryText
                )
            }

            if (!device.isCurrentDevice) {
                NestButton(
                    text = "Revoke",
                    onClick = onRevoke,
                    variant = NestButtonVariant.OUTLINE,
                    testTag = "revoke_device_${device.id}"
                )
            }
        }
    }
}

package com.example.feature.auth

import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestColumn
import com.example.core.designsystem.NestDialog
import com.example.core.designsystem.NestTable
import com.example.core.designsystem.NestTextField
import com.example.core.designsystem.NestTopBar
import com.example.core.security.AuthMode
import com.example.core.security.NestBiometricLockManager
import com.example.core.security.TrustedDevice
import com.example.ui.theme.LocalNestColors

private fun Context.findFragmentActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalNestColors.current
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val biometricLockManager = remember { NestBiometricLockManager() }

    val triggerBiometricAuth = {
        if (activity != null) {
            biometricLockManager.authenticateBiometric(
                activity = activity,
                title = "Unlock Nest Vault",
                subtitle = "Scan fingerprint or facial recognition to unlock",
                description = "Hardware-backed zero-knowledge biometric verification.",
                negativeButtonText = "Use Master Password",
                onSuccess = {
                    viewModel.loginWithBiometrics(onAuthSuccess)
                },
                onError = { err ->
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            viewModel.loginWithBiometrics(onAuthSuccess)
        }
    }

    // Modal Dialog showing generated Recovery Key after Registration
    if (uiState.showRecoveryKeyDialog && uiState.generatedRecoveryKey != null) {
        NestDialog(
            title = "Zero-Knowledge Recovery Key",
            message = "Save this key in a physical safe. It is the ONLY way to recover your vault if you forget your Master Password:\n\n${uiState.generatedRecoveryKey}",
            confirmText = "I Saved My Key",
            dismissText = "Copy Key",
            onConfirm = { viewModel.dismissRecoveryKeyDialog() },
            onDismiss = { viewModel.dismissRecoveryKeyDialog() },
            testTag = "auth_recovery_key_dialog"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        NestTopBar(
            title = "Authentication & Access",
            subtitle = "Zero-Knowledge Session Management"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selector Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val modes = listOf(
                    AuthMode.LOGIN to "Login",
                    AuthMode.REGISTER to "Register",
                    AuthMode.FORGOT_PASSWORD to "Recovery Key",
                    AuthMode.TRUSTED_DEVICES to "Trusted Devices"
                )

                items(modes) { (mode, title) ->
                    val isSelected = uiState.currentMode == mode
                    val shape = RoundedCornerShape(20.dp)

                    Box(
                        modifier = Modifier
                            .testTag("auth_tab_${mode.name.lowercase()}")
                            .clip(shape)
                            .background(if (isSelected) colors.primaryAccent else colors.card)
                            .clickable { viewModel.setMode(mode) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.card else colors.primaryText
                        )
                    }
                }
            }

            // Messages feedback
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = colors.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            if (uiState.successMessage != null) {
                Text(
                    text = uiState.successMessage!!,
                    color = colors.success,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Active Form Content
            when (uiState.currentMode) {
                AuthMode.LOGIN -> {
                    LoginFormCard(
                        uiState = uiState,
                        onEmailChange = { viewModel.updateEmail(it) },
                        onPasswordChange = { viewModel.updatePassword(it) },
                        onSubmit = { viewModel.login(onAuthSuccess) },
                        onBiometricsClick = { triggerBiometricAuth() }
                    )
                }
                AuthMode.REGISTER -> {
                    RegisterFormCard(
                        uiState = uiState,
                        onEmailChange = { viewModel.updateEmail(it) },
                        onPasswordChange = { viewModel.updatePassword(it) },
                        onConfirmPasswordChange = { viewModel.updateConfirmPassword(it) },
                        onSubmit = { viewModel.register(onAuthSuccess) }
                    )
                }
                AuthMode.FORGOT_PASSWORD -> {
                    ForgotPasswordCard(
                        uiState = uiState,
                        onEmailChange = { viewModel.updateEmail(it) },
                        onRecoveryKeyChange = { viewModel.updateRecoveryKeyInput(it) },
                        onNewPasswordChange = { viewModel.updatePassword(it) },
                        onConfirmPasswordChange = { viewModel.updateConfirmPassword(it) },
                        onSubmit = { viewModel.recoverAccount(onAuthSuccess) }
                    )
                }
                AuthMode.TRUSTED_DEVICES -> {
                    TrustedDevicesCard(
                        devices = uiState.trustedDevices,
                        onRevokeDevice = { viewModel.revokeTrustedDevice(it) }
                    )
                }
            }

            // Session Status Bento Card
            if (uiState.activeSession != null) {
                NestCard(
                    cornerRadius = 28.dp,
                    padding = 20.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.success.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = colors.success
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Active Vault Session",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                            Text(
                                text = "Token: ${uiState.activeSession?.sessionToken}",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.secondaryText
                            )
                        }

                        NestButton(
                            text = "Lock Vault",
                            onClick = { viewModel.logout() },
                            variant = NestButtonVariant.OUTLINE,
                            icon = Icons.Default.PowerSettingsNew,
                            testTag = "auth_session_lock_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginFormCard(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBiometricsClick: () -> Unit
) {
    NestCard(
        cornerRadius = 28.dp,
        padding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Authenticate Master Vault",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = LocalNestColors.current.primaryText
            )

            NestTextField(
                value = uiState.emailInput,
                onValueChange = onEmailChange,
                label = "Vault Account Email",
                placeholder = "user@nordic.vault",
                testTag = "login_email_input"
            )

            NestTextField(
                value = uiState.passwordInput,
                onValueChange = onPasswordChange,
                label = "Master Password / PIN",
                placeholder = "••••••••",
                isPassword = true,
                testTag = "login_password_input"
            )

            NestButton(
                text = if (uiState.isLoading) "Authenticating..." else "Unlock Vault",
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                variant = NestButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_submit_button"
            )

            NestButton(
                text = "Biometric Passcode Verification",
                onClick = onBiometricsClick,
                variant = NestButtonVariant.OUTLINE,
                icon = Icons.Default.Fingerprint,
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_biometrics_button"
            )
        }
    }
}

@Composable
private fun RegisterFormCard(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    NestCard(
        cornerRadius = 28.dp,
        padding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Create Digital Vault Account",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = LocalNestColors.current.primaryText
            )

            NestTextField(
                value = uiState.emailInput,
                onValueChange = onEmailChange,
                label = "Vault Account Email",
                placeholder = "user@nordic.vault",
                testTag = "register_email_input"
            )

            NestTextField(
                value = uiState.passwordInput,
                onValueChange = onPasswordChange,
                label = "Master Password (min 8 chars)",
                placeholder = "••••••••",
                isPassword = true,
                testTag = "register_password_input"
            )

            NestTextField(
                value = uiState.confirmPasswordInput,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Master Password",
                placeholder = "••••••••",
                isPassword = true,
                testTag = "register_confirm_password_input"
            )

            NestButton(
                text = if (uiState.isLoading) "Registering..." else "Create Account & Generate Recovery Key",
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                variant = NestButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
                testTag = "register_submit_button"
            )
        }
    }
}

@Composable
private fun ForgotPasswordCard(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onRecoveryKeyChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    NestCard(
        cornerRadius = 28.dp,
        padding = 20.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Recover Vault with Recovery Key",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = LocalNestColors.current.primaryText
            )

            NestTextField(
                value = uiState.emailInput,
                onValueChange = onEmailChange,
                label = "Account Email",
                placeholder = "user@nordic.vault",
                testTag = "recover_email_input"
            )

            NestTextField(
                value = uiState.recoveryKeyInput,
                onValueChange = onRecoveryKeyChange,
                label = "Zero-Knowledge Recovery Key",
                placeholder = "e.g. NEST-8849-2040-NORDIC",
                testTag = "recover_key_input"
            )

            NestTextField(
                value = uiState.passwordInput,
                onValueChange = onNewPasswordChange,
                label = "New Master Password",
                placeholder = "••••••••",
                isPassword = true,
                testTag = "recover_new_password_input"
            )

            NestTextField(
                value = uiState.confirmPasswordInput,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm New Master Password",
                placeholder = "••••••••",
                isPassword = true,
                testTag = "recover_confirm_password_input"
            )

            NestButton(
                text = if (uiState.isLoading) "Resetting Password..." else "Reset Master Password",
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                variant = NestButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
                testTag = "recover_submit_button"
            )
        }
    }
}

@Composable
private fun TrustedDevicesCard(
    devices: List<TrustedDevice>,
    onRevokeDevice: (String) -> Unit
) {
    val colors = LocalNestColors.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Authorized Hardware Devices",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            modifier = Modifier.padding(start = 4.dp)
        )

        NestTable(
            headers = listOf("Device", "Last Active", "Action"),
            items = devices,
            columns = listOf(
                NestColumn(
                    title = "Device Name",
                    weight = 2f,
                    cellContent = { device ->
                        Column {
                            Text(
                                text = device.deviceName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primaryText
                            )
                            Text(
                                text = "IP: ${device.ipAddress}",
                                fontSize = 11.sp,
                                color = colors.secondaryText
                            )
                        }
                    }
                ),
                NestColumn(
                    title = "Status",
                    weight = 1.2f,
                    cellContent = { device ->
                        Text(
                            text = device.lastActiveTime,
                            fontSize = 12.sp,
                            color = if (device.isCurrentDevice) colors.success else colors.secondaryText,
                            fontWeight = if (device.isCurrentDevice) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                ),
                NestColumn(
                    title = "Trust Level",
                    weight = 1f,
                    cellContent = { device ->
                        if (device.isCurrentDevice) {
                            Text(
                                text = "Current",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryAccent
                            )
                        } else {
                            NestButton(
                                text = "Revoke",
                                onClick = { onRevokeDevice(device.id) },
                                variant = NestButtonVariant.GHOST,
                                testTag = "revoke_device_${device.id}"
                            )
                        }
                    }
                )
            )
        )
    }
}

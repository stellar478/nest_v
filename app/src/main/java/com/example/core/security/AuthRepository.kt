package com.example.core.security

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

interface AuthRepository {
    fun getCurrentAccount(): UserAccount?
    fun registerAccount(email: String, masterPassword: String): Result<Pair<UserAccount, String>>
    fun login(email: String, masterPassword: String): Result<UserSession>
    fun loginWithBiometrics(): Result<UserSession>
    fun recoverAccount(email: String, recoveryKey: String, newMasterPassword: String): Result<Boolean>
    fun getTrustedDevices(): List<TrustedDevice>
    fun revokeTrustedDevice(deviceId: String): List<TrustedDevice>
    fun addTrustedDevice(deviceName: String, ipAddress: String): TrustedDevice
    fun getCurrentSession(): UserSession?
    fun logout()
}

class NestAuthRepository(
    private val keyManager: VaultKeyManager = NestVaultKeyManager()
) : AuthRepository {

    // Mock initial user account stored safely in memory/hardware-keystore simulator
    private var registeredAccount: UserAccount? = UserAccount(
        email = "user@nordic.vault",
        masterPasswordHash = hashPassword("MasterPass123!"),
        recoveryKey = "NEST-8849-2040-NORDIC",
        isBiometricEnabled = true
    )

    private var activeSession: UserSession? = UserSession(
        sessionToken = "nest_session_" + UUID.randomUUID().toString().take(8),
        email = "user@nordic.vault"
    )

    private val trustedDevicesList = mutableListOf(
        TrustedDevice(
            id = "dev_01",
            deviceName = "Nordic Pixel 8 Pro (This Device)",
            ipAddress = "192.168.1.104",
            lastActiveTime = "Active Now",
            isCurrentDevice = true,
            isTrusted = true
        ),
        TrustedDevice(
            id = "dev_02",
            deviceName = "MacBook Pro M2 (Vault Web)",
            ipAddress = "10.0.0.42",
            lastActiveTime = "2 hours ago",
            isCurrentDevice = false,
            isTrusted = true
        ),
        TrustedDevice(
            id = "dev_03",
            deviceName = "iPad Air (Vault Mobile)",
            ipAddress = "10.0.0.55",
            lastActiveTime = "Yesterday",
            isCurrentDevice = false,
            isTrusted = true
        )
    )

    override fun getCurrentAccount(): UserAccount? = registeredAccount

    override fun registerAccount(email: String, masterPassword: String): Result<Pair<UserAccount, String>> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (masterPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Master password must be at least 8 characters long."))
        }

        val generatedRecoveryKey = generateRecoveryKey()
        val newAccount = UserAccount(
            email = email,
            masterPasswordHash = hashPassword(masterPassword),
            recoveryKey = generatedRecoveryKey
        )

        registeredAccount = newAccount
        activeSession = UserSession(
            sessionToken = "nest_session_" + UUID.randomUUID().toString().take(8),
            email = email
        )

        keyManager.setupMasterPin(masterPassword)

        return Result.success(Pair(newAccount, generatedRecoveryKey))
    }

    override fun login(email: String, masterPassword: String): Result<UserSession> {
        val account = registeredAccount
        if (account == null) {
            return Result.failure(IllegalStateException("No registered account found. Please register first."))
        }

        if (account.email.lowercase() != email.lowercase()) {
            return Result.failure(IllegalArgumentException("Invalid email address."))
        }

        if (account.masterPasswordHash != hashPassword(masterPassword) && !keyManager.verifyMasterPin(masterPassword)) {
            return Result.failure(IllegalArgumentException("Incorrect master password."))
        }

        val session = UserSession(
            sessionToken = "nest_session_" + UUID.randomUUID().toString().take(8),
            email = email
        )
        activeSession = session
        return Result.success(session)
    }

    override fun loginWithBiometrics(): Result<UserSession> {
        val account = registeredAccount ?: return Result.failure(IllegalStateException("No account registered."))
        val session = UserSession(
            sessionToken = "nest_bio_session_" + UUID.randomUUID().toString().take(8),
            email = account.email
        )
        activeSession = session
        return Result.success(session)
    }

    override fun recoverAccount(
        email: String,
        recoveryKey: String,
        newMasterPassword: String
    ): Result<Boolean> {
        val account = registeredAccount
        if (account == null) {
            return Result.failure(IllegalStateException("No registered account found."))
        }

        if (account.email.lowercase() != email.lowercase()) {
            return Result.failure(IllegalArgumentException("Account email does not match."))
        }

        val cleanKey = recoveryKey.trim().uppercase()
        if (account.recoveryKey.replace("-", "") != cleanKey.replace("-", "")) {
            return Result.failure(IllegalArgumentException("Invalid Recovery Key."))
        }

        if (newMasterPassword.length < 8) {
            return Result.failure(IllegalArgumentException("New Master Password must be at least 8 characters."))
        }

        registeredAccount = account.copy(
            masterPasswordHash = hashPassword(newMasterPassword)
        )
        keyManager.setupMasterPin(newMasterPassword)

        return Result.success(true)
    }

    override fun getTrustedDevices(): List<TrustedDevice> = trustedDevicesList.toList()

    override fun revokeTrustedDevice(deviceId: String): List<TrustedDevice> {
        trustedDevicesList.removeAll { it.id == deviceId && !it.isCurrentDevice }
        return getTrustedDevices()
    }

    override fun addTrustedDevice(deviceName: String, ipAddress: String): TrustedDevice {
        val newDevice = TrustedDevice(
            deviceName = deviceName,
            ipAddress = ipAddress,
            lastActiveTime = "Just now",
            isCurrentDevice = false,
            isTrusted = true
        )
        trustedDevicesList.add(newDevice)
        return newDevice
    }

    override fun getCurrentSession(): UserSession? = activeSession

    override fun logout() {
        activeSession = null
    }

    private fun hashPassword(password: String): String {
        // Zero-knowledge hash representation
        return "sha256_nest_vault_" + password.hashCode()
    }

    private fun generateRecoveryKey(): String {
        val part1 = (1000..9999).random()
        val part2 = (1000..9999).random()
        return "NEST-$part1-$part2-NORDIC"
    }
}

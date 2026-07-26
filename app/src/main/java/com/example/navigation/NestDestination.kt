package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NestDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object VaultHome : NestDestination("vault_home", "Vault", Icons.Default.Shield, "nav_vault_home")
    object Auth : NestDestination("auth", "Access", Icons.Default.LockOpen, "nav_auth")
    object SecurityAudit : NestDestination("security_audit", "Audit", Icons.Default.Security, "nav_security_audit")
    object Settings : NestDestination("settings", "Settings", Icons.Default.Settings, "nav_settings")

    object ItemDetail : NestDestination("item_detail/{itemId}", "Item Detail", Icons.Default.Key, "nav_item_detail") {
        fun createRoute(itemId: String) = "item_detail/$itemId"
    }

    object ItemEditor : NestDestination("item_editor?itemId={itemId}", "Edit Item", Icons.Default.Key, "nav_item_editor") {
        fun createRoute(itemId: String? = null) = if (itemId != null) "item_editor?itemId=$itemId" else "item_editor"
    }
}

val mainNavItems = listOf(
    NestDestination.VaultHome,
    NestDestination.Auth,
    NestDestination.SecurityAudit,
    NestDestination.Settings
)

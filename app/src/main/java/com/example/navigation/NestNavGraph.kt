package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.auth.AuthScreen
import com.example.feature.items.ItemDetailScreen
import com.example.feature.items.ItemEditorScreen
import com.example.feature.security.SecurityAuditScreen
import com.example.feature.settings.SettingsScreen
import com.example.feature.vault.VaultHomeScreen

@Composable
fun NestNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NestDestination.VaultHome.route,
        modifier = modifier
    ) {
        composable(NestDestination.VaultHome.route) {
            VaultHomeScreen(
                onNavigateToDetail = { itemId ->
                    navController.navigate(NestDestination.ItemDetail.createRoute(itemId))
                },
                onNavigateToCreate = {
                    navController.navigate(NestDestination.ItemEditor.createRoute())
                },
                onNavigateToAudit = {
                    navController.navigate(NestDestination.SecurityAudit.route)
                }
            )
        }

        composable(NestDestination.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(NestDestination.VaultHome.route) {
                        popUpTo(NestDestination.VaultHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NestDestination.SecurityAudit.route) {
            SecurityAuditScreen()
        }

        composable(NestDestination.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = NestDestination.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(NestDestination.ItemEditor.createRoute(id))
                },
                onDeleteClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NestDestination.ItemEditor.route,
            arguments = listOf(navArgument("itemId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemEditorScreen(
                itemId = itemId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}

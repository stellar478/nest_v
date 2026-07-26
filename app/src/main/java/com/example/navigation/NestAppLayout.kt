package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.designsystem.NestBottomBar
import com.example.core.designsystem.NestNavItem
import com.example.core.designsystem.NestNavRail
import com.example.ui.theme.LocalNestColors

@Composable
fun NestAppLayout() {
    val navController = rememberNavController()
    val colors = LocalNestColors.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NestDestination.VaultHome.route

    val navItems = mainNavItems.map { destination ->
        NestNavItem(
            route = destination.route,
            title = destination.title,
            icon = destination.icon,
            testTag = destination.testTag
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                NestNavRail(
                    items = navItems,
                    currentRoute = currentRoute,
                    onItemSelected = { item ->
                        navController.navigate(item.route) {
                            popUpTo(NestDestination.VaultHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NestNavGraph(
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Scaffold(
                bottomBar = {
                    val isMainScreen = mainNavItems.any { it.route == currentRoute }
                    if (isMainScreen) {
                        NestBottomBar(
                            items = navItems,
                            currentRoute = currentRoute,
                            onItemSelected = { item ->
                                navController.navigate(item.route) {
                                    popUpTo(NestDestination.VaultHome.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                },
                contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                NestNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

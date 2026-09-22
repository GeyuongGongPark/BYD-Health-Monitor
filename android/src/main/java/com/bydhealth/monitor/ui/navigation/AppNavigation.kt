package com.bydhealth.monitor.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.bydhealth.monitor.ui.dashboard.DashboardScreen
import com.bydhealth.monitor.ui.maintenance.MaintenanceHistoryScreen
import com.bydhealth.monitor.ui.maintenance.MaintenanceScreen
import com.bydhealth.monitor.ui.malfunction.MalfunctionScreen
import com.bydhealth.monitor.ui.pairing.PairingScreen
import com.bydhealth.monitor.ui.theme.*
import com.bydhealth.monitor.ui.tyre.TyreScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard   : Screen("dashboard",   "대시보드", Icons.Default.Home)
    object Malfunction : Screen("malfunction", "고장",    Icons.Default.Warning)
    object Tyre        : Screen("tyre",        "타이어",   Icons.Default.Speed)
    object Maintenance : Screen("maintenance", "정비",    Icons.Default.Build)
    object Pairing     : Screen("pairing",     "연결",    Icons.Default.PhoneAndroid)
}

private val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Malfunction,
    Screen.Tyre,
    Screen.Maintenance,
    Screen.Pairing,
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            NavigationBar(containerColor = SurfaceDark, tonalElevation = 0.dp) {
                val navBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStack?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BydBlueBright,
                            selectedTextColor = BydBlueBright,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = BydBlueDim,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onMalfunctionClick = { navController.navigate(Screen.Malfunction.route) },
                    onTyreClick = { navController.navigate(Screen.Tyre.route) },
                    onMaintenanceClick = { navController.navigate(Screen.Maintenance.route) },
                )
            }
            composable(Screen.Malfunction.route) { MalfunctionScreen() }
            composable(Screen.Tyre.route) { TyreScreen() }
            composable(Screen.Maintenance.route) {
                MaintenanceScreen(
                    onHistoryClick = { navController.navigate("maintenance_history") },
                )
            }
            composable("maintenance_history") {
                MaintenanceHistoryScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Pairing.route) { PairingScreen() }
        }
    }
}

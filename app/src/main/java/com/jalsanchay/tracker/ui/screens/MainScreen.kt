package com.jalsanchay.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.WaterDropLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    
    val items = listOf(
        Triple(Screen.DashboardHome, Icons.Default.Home, "Home"),
        Triple(Screen.LogRainfall, Icons.Default.AddCircle, "Log"),
        Triple(Screen.History, Icons.Default.List, "History"),
        Triple(Screen.Report, Icons.Default.BarChart, "Report"),
        Triple(Screen.Tips, Icons.Default.Lightbulb, "Tips")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        WaterDropLogo(size = 32.dp, animated = false)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Jal-Sanchay Tracker", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1B2A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0D1B2A),
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { (screen, icon, label) ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(Screen.DashboardHome.route)
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00BCD4),
                            unselectedIconColor = Color(0xFF546E7A),
                            selectedTextColor = Color(0xFF00BCD4),
                            unselectedTextColor = Color(0xFF546E7A),
                            indicatorColor = Color(0x1A00BCD4)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.DashboardHome.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.DashboardHome.route) { DashboardScreen(bottomNavController) }
            composable(Screen.LogRainfall.route) { LogRainfallScreen(bottomNavController) }
            composable(Screen.History.route) { HistoryScreen(bottomNavController) }
            composable(Screen.Report.route) { ReportScreen(bottomNavController) }
            composable(Screen.Tips.route) { TipsScreen(bottomNavController) }
        }
    }
}

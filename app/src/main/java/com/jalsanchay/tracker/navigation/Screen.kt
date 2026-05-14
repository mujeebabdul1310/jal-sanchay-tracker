package com.jalsanchay.tracker.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    
    // Bottom Nav Screens (Nested in Main)
    object DashboardHome : Screen("dashboard_home")
    object LogRainfall : Screen("log_rainfall")
    object History : Screen("history")
    object Report : Screen("report")
    object Tips : Screen("tips")
    
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

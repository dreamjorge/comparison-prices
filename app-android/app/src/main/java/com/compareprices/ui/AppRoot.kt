package com.compareprices.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier
import com.compareprices.ui.home.HomeScreen
import com.compareprices.ui.history.PriceHistoryScreen
import com.compareprices.ui.settings.SettingsScreen

private sealed class Screen(val route: String, val label: String = "") {
  data object Home : Screen("home", "Lista")
  data object Compare : Screen("compare", "Comparar")
  data object Settings : Screen("settings", "Ajustes")
  data object Paywall : Screen("paywall")
  data object PriceHistory : Screen("priceHistory/{productId}") {
    fun createRoute(productId: Long) = "priceHistory/$productId"
  }
}

@Composable
fun AppRoot() {
  val navController = rememberNavController()
  val items = listOf(Screen.Home, Screen.Compare, Screen.Settings)
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  Scaffold(
    bottomBar = {
      // Only show bottom bar on main tabs, not on detail screens
      if (currentRoute in items.map { it.route }) {
        NavigationBar {
          items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
              selected = selected,
              onClick = {
                navController.navigate(screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              icon = {
                when (screen) {
                  Screen.Home -> Icon(Icons.Outlined.Home, contentDescription = screen.label)
                  Screen.Compare -> Icon(Icons.Outlined.Storefront, contentDescription = screen.label)
                  Screen.Settings -> Icon(Icons.Outlined.Settings, contentDescription = screen.label)
                  else -> {}
                }
              },
              label = { Text(screen.label) }
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier.padding(innerPadding)
    ) {
<<<<<<< HEAD
      composable(Screen.Home.route) {
        HomeScreen(
          onNavigateToHistory = { productId ->
            navController.navigate(Screen.PriceHistory.createRoute(productId))
          }
        )
      }
      composable(Screen.Compare.route) {
        com.compareprices.ui.compare.CompareScreen()
      }
      composable(Screen.Settings.route) {
        SettingsScreen(
          onNavigateToPaywall = {
            navController.navigate(Screen.Paywall.route)
          }
        )
      }
      composable(Screen.Paywall.route) {
        com.compareprices.ui.premium.PaywallScreen(
          onDismiss = { navController.navigateUp() }
        )
      }
      composable(
        route = Screen.PriceHistory.route,
        arguments = listOf(navArgument("productId") { type = NavType.LongType })
      ) {
        PriceHistoryScreen(
          productId = it.arguments?.getLong("productId") ?: 0L,
          onNavigateBack = { navController.navigateUp() }
        )
=======
      composable(Screen.Home.route) { 
        HomeScreen(
          onNavigateToHistory = { id, name -> navController.navigate("price_history/$id/$name") },
          onNavigateToPaywall = { navController.navigate("paywall") }
        ) 
      }
      composable(Screen.Compare.route) { com.compareprices.ui.compare.CompareScreen() }
      composable(Screen.Settings.route) { SettingsScreen() }
      composable("paywall") {
        val premiumViewModel: com.compareprices.ui.premium.PremiumViewModel = androidx.hilt.navigation.compose.hiltViewModel()
        com.compareprices.ui.premium.PaywallScreen(
          userPrefs = premiumViewModel.userPrefs,
          onBack = { navController.popBackStack() }
        )
      }
      composable("price_history/{productId}/{productName}") { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull() ?: 0L
        val productName = backStackEntry.arguments?.getString("productName") ?: ""
        com.compareprices.ui.history.PriceHistoryScreen(productId, productName)
>>>>>>> feature/develop-tickets
      }
    }
  }
}

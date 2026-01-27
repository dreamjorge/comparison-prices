package com.compareprices.ui

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.compareprices.ui.home.HomeScreen

private sealed class Screen(val route: String, val label: String) {
  data object Home : Screen("home", "Lista")
  data object Compare : Screen("compare", "Comparar")
  data object Settings : Screen("settings", "Ajustes")
}

@Composable
fun AppRoot() {
  val navController = rememberNavController()
  val items = listOf(Screen.Home, Screen.Compare, Screen.Settings)
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  Scaffold(
    bottomBar = {
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
              }
            },
            label = { Text(screen.label) }
          )
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable(Screen.Home.route) { HomeScreen() }
      composable(Screen.Compare.route) { CompareScreen() }
      composable(Screen.Settings.route) { SettingsScreen() }
    }
  }
}

@Composable
private fun CompareScreen() {
  PlaceholderScreen(label = "Comparador por tienda")
}

@Composable
private fun SettingsScreen() {
  PlaceholderScreen(label = "Configuracion")
}

@Composable
private fun PlaceholderScreen(label: String) {
  androidx.compose.foundation.layout.Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = androidx.compose.ui.Alignment.Center
  ) {
    Text(text = label)
  }
}

package com.compareprices.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
  onNavigateToPaywall: () -> Unit = {},
  viewModel: com.compareprices.ui.premium.PremiumViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
  val isPremium by viewModel.isPremium.collectAsState()
  val sections = defaultSettingsSections(isPremium)

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      SettingsHeader()
    }

    sections.forEach { section ->
      item {
        SettingsSectionCard(section, onNavigateToPaywall)
      }
    }
  }
}

@Composable
private fun SettingsHeader() {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = "Ajustes",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = "Configura tus tiendas favoritas, alertas y plan de la app.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun SettingsSectionCard(
  section: SettingsSection,
  onNavigateToPaywall: () -> Unit
) {
  SettingsCard(title = section.title) {
    section.items.forEach { item ->
      SettingsRow(
        icon = { SettingsIconBadge(item.icon) },
        title = item.title,
        value = item.value,
        onClick = {
          if (item.title == "Plan actual" || item.title == "Upgrade") {
            onNavigateToPaywall()
          }
        }
      )
    }
  }
}

@Composable
private fun SettingsCard(
  title: String,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      content()
    }
  }
}

@Composable
private fun SettingsRow(
  icon: @Composable () -> Unit,
  title: String,
  value: String,
  onClick: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Surface(
      tonalElevation = 2.dp,
      shape = MaterialTheme.shapes.small,
      color = MaterialTheme.colorScheme.surfaceVariant
    ) {
      Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        icon()
      }
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, style = MaterialTheme.typography.bodyMedium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun SettingsIconBadge(icon: SettingsIcon) {
  val vector = when (icon) {
    SettingsIcon.Location -> Icons.Outlined.LocationOn
    SettingsIcon.Storefront -> Icons.Outlined.Storefront
    SettingsIcon.Notifications -> Icons.Outlined.NotificationsActive
    SettingsIcon.Payments -> Icons.Outlined.Payments
    SettingsIcon.Info -> Icons.Outlined.Info
  }
  Icon(vector, contentDescription = null)
}

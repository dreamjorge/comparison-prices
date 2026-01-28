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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      SettingsHeader()
    }

    item {
      PreferencesCard()
    }

    item {
      AlertsCard()
    }

    item {
      PlanCard()
    }

    item {
      AboutCard()
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
private fun PreferencesCard() {
  SettingsCard(title = "Preferencias de compra") {
    SettingsRow(
      icon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
      title = "Zona",
      value = "Centro - CDMX"
    )
    SettingsRow(
      icon = { Icon(Icons.Outlined.Storefront, contentDescription = null) },
      title = "Tiendas activas",
      value = "Walmart, Soriana, Chedraui"
    )
  }
}

@Composable
private fun AlertsCard() {
  SettingsCard(title = "Alertas y precios") {
    SettingsRow(
      icon = { Icon(Icons.Outlined.NotificationsActive, contentDescription = null) },
      title = "Alertas",
      value = "Baja de precio semanal"
    )
    SettingsRow(
      icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
      title = "Meta de ahorro",
      value = "Ahorrar $120 esta semana"
    )
  }
}

@Composable
private fun PlanCard() {
  SettingsCard(title = "Plan") {
    SettingsRow(
      icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
      title = "Plan actual",
      value = "Free con anuncios"
    )
    SettingsRow(
      icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
      title = "Upgrade",
      value = "Pro: sin anuncios + historial extendido"
    )
  }
}

@Composable
private fun AboutCard() {
  SettingsCard(title = "Acerca de") {
    SettingsRow(
      icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
      title = "Version",
      value = "MVP 0.1"
    )
    SettingsRow(
      icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
      title = "Soporte",
      value = "hola@comparador.app"
    )
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
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
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

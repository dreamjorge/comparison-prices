package com.compareprices.ui.settings

data class SettingsSection(
  val title: String,
  val items: List<SettingsItem>
)

data class SettingsItem(
  val title: String,
  val value: String,
  val icon: SettingsIcon
)

enum class SettingsIcon {
  Location,
  Storefront,
  Notifications,
  Payments,
  Info
}

fun defaultSettingsSections(isPremium: Boolean = false): List<SettingsSection> =
  listOf(
    // ... existing sections ...
    SettingsSection(
      title = "Preferencias de compra",
      items = listOf(
        SettingsItem(
          title = "Zona",
          value = "Centro - CDMX",
          icon = SettingsIcon.Location
        ),
        SettingsItem(
          title = "Tiendas activas",
          value = "Walmart, Soriana, Chedraui",
          icon = SettingsIcon.Storefront
        )
      )
    ),
    SettingsSection(
      title = "Alertas y precios",
      items = listOf(
        SettingsItem(
          title = "Alertas",
          value = "Baja de precio semanal",
          icon = SettingsIcon.Notifications
        ),
        SettingsItem(
          title = "Meta de ahorro",
          value = "Ahorrar $120 esta semana",
          icon = SettingsIcon.Payments
        ),
        SettingsItem(
          title = "Origen de precios",
          value = "Local demo (remoto en preparación)",
          icon = SettingsIcon.Info
        )
      )
    ),
    SettingsSection(
      title = "Plan",
      items = if (isPremium) {
        listOf(
          SettingsItem(
            title = "Plan actual",
            value = "Pro (Sin anuncios)",
            icon = SettingsIcon.Payments
          )
        )
      } else {
        listOf(
          SettingsItem(
            title = "Plan actual",
            value = "Free con anuncios",
            icon = SettingsIcon.Payments
          ),
          SettingsItem(
            title = "Upgrade",
            value = "Quitar anuncios",
            icon = SettingsIcon.Payments
          )
        )
      }
    ),
    SettingsSection(
      title = "Acerca de",
      items = listOf(
        SettingsItem(
          title = "Version",
          value = "MVP 0.1",
          icon = SettingsIcon.Info
        ),
        SettingsItem(
          title = "Soporte",
          value = "hola@comparador.app",
          icon = SettingsIcon.Info
        )
      )
    )
  )

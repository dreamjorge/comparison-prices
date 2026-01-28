package com.compareprices.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsContentTest {
  @Test
  fun `default sections include expected titles`() {
    val sections = defaultSettingsSections()

    val titles = sections.map { it.title }

    assertEquals(
      listOf("Preferencias de compra", "Alertas y precios", "Plan", "Acerca de"),
      titles
    )
  }

  @Test
  fun `default sections include at least two items each`() {
    val sections = defaultSettingsSections()

    assertTrue(sections.all { it.items.size >= 2 })
  }
}

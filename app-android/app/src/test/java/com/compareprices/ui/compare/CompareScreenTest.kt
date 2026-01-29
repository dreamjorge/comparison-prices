package com.compareprices.ui.compare

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CompareScreenTest {
  @Test
  fun `blank query returns all stores`() {
    val stores = demoStorePrices()

    val filtered = filterStorePrices("  ", stores)

    assertEquals(stores.size, filtered.size)
  }

  @Test
  fun `filters by store name`() {
    val stores = demoStorePrices()

    val filtered = filterStorePrices("Ahorro", stores)

    assertEquals(listOf("Ahorro Max"), filtered.map { it.storeName })
  }

  @Test
  fun `filters by product name`() {
    val stores = demoStorePrices()

    val filtered = filterStorePrices("Arroz", stores)

    assertEquals(4, filtered.size)
  }

  @Test
  fun `sorts stores by total price ascending`() {
    val stores = demoStorePrices()

    val sorted = sortStorePricesByTotal(stores)

    assertEquals(
      listOf("Walmart", "Mercado Central", "Super Norte", "Ahorro Max"),
      sorted.map { it.storeName }
    )
  }

  @Test
  fun `parses total price labels`() {
    assertEquals(1580, parseTotalPrice("$ 1.580"))
  }

  @Test
  fun `builds savings against the next cheapest store`() {
    val stores = demoStorePrices()

    val comparisons = buildStoreComparisons(sortStorePricesByTotal(stores))

    assertEquals(listOf(30, 140, 270, null), comparisons.map { it.savingsVsNext })
  }

  @Test
  fun `formats currency using locale conventions`() {
    val formatted = formatCurrency(4560, Locale.US)

    assertEquals("$4,560", formatted)
  }

  @Test
  fun `formats date using locale and timezone`() {
    val formatted = formatTodayLabel(
      date = Date(0),
      locale = Locale.US,
      timeZone = TimeZone.getTimeZone("UTC")
    )

    assertEquals("1 Jan 1970", formatted)
  }
}

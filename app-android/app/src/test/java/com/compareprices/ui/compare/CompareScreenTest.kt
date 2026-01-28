package com.compareprices.ui.compare

import org.junit.Assert.assertEquals
import org.junit.Test

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
}

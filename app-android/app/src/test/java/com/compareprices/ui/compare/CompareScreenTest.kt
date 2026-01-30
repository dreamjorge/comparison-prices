package com.compareprices.ui.compare

import com.compareprices.data.local.ListItemEntity
import com.compareprices.data.local.ListItemWithProduct
import com.compareprices.data.local.ProductEntity
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
  fun `filters store prices to items from the list`() {
    val stores = demoStorePrices()
    val listItems = listOf(
      ListItemWithProduct(
        item = ListItemEntity(id = 1, listId = 1, productId = 10, quantity = 1.0, unit = "unidad"),
        product = ProductEntity(id = 10, name = "Leche entera", brand = "La Serenisima")
      ),
      ListItemWithProduct(
        item = ListItemEntity(id = 2, listId = 1, productId = 11, quantity = 1.0, unit = "unidad"),
        product = ProductEntity(id = 11, name = "Pan integral", brand = "Bimbo")
      )
    )

    val filtered = filterStorePricesByList(stores, listItems)

    assertEquals(4, filtered.size)
    assertEquals(listOf("Leche entera", "Pan integral"), filtered.first().items.map { it.product })
  }

  @Test
  fun `sorts stores by total price ascending`() {
    val stores = demoStorePrices()

    val sorted = sortStorePricesByTotal(stores, emptyMap())

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

    val comparisons = buildStoreComparisons(
      storePrices = sortStorePricesByTotal(stores, emptyMap()),
      quantityByProduct = emptyMap()
    )

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

  @Test
  fun `totals reflect list quantities`() {
    val store = demoStorePrices().first()
    val quantities = mapOf("leche entera" to 2.0, "pan integral" to 3.0)

    val total = storeTotalValue(store, quantities, Locale("es", "AR"))

    assertEquals(8340, total)
  }

  @Test
  fun `builds quantity map by product name`() {
    val listItems = listOf(
      ListItemWithProduct(
        item = ListItemEntity(id = 1, listId = 1, productId = 10, quantity = 2.0, unit = "L"),
        product = ProductEntity(id = 10, name = "Leche entera", brand = "La Serenisima")
      ),
      ListItemWithProduct(
        item = ListItemEntity(id = 2, listId = 1, productId = 11, quantity = 1.0, unit = "unidad"),
        product = ProductEntity(id = 11, name = "Pan integral", brand = "Bimbo")
      )
    )

    val quantities = buildQuantityByProduct(listItems, Locale("es", "AR"))

    assertEquals(mapOf("leche entera" to 2.0, "pan integral" to 1.0), quantities)
  }

  @Test
  fun `rounds totals after summing fractional quantities`() {
    val store = StorePrice(
      storeName = "Test",
      zone = "Zone",
      items = listOf(
        StoreItemPrice(product = "Item A", price = "$ 101"),
        StoreItemPrice(product = "Item B", price = "$ 101")
      )
    )
    val quantities = mapOf("item a" to 0.5, "item b" to 0.5)

    val total = storeTotalValue(store, quantities, Locale("es", "AR"))

    assertEquals(101, total)
  }
}

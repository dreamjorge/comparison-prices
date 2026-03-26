package com.compareprices.data.work

import com.compareprices.data.local.ListItemEntity
import com.compareprices.data.local.ListItemWithProduct
import com.compareprices.data.local.ProductEntity
import com.compareprices.ui.compare.buildQuantityByProduct
import com.compareprices.ui.compare.demoStorePrices
import com.compareprices.ui.compare.sortStorePricesByTotal
import com.compareprices.ui.compare.storeTotalValue
import com.compareprices.ui.components.displayBrand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class PriceRefreshWorkerLogicTest {

  // --- findCheapestStore via sortStorePricesByTotal ---

  @Test
  fun `cheapest store is first after sorting`() {
    val stores = demoStorePrices()
    val sorted = sortStorePricesByTotal(stores, emptyMap(), Locale.US)

    assertEquals("Walmart", sorted.first().storeName)
  }

  @Test
  fun `cheapest store changes with list quantities`() {
    // Adding lots of Arroz largo fino (most expensive in Walmart at 1920 vs 1880 in Super Norte)
    val quantities = mapOf("arroz largo fino" to 10.0)
    val stores = demoStorePrices()
    val sorted = sortStorePricesByTotal(stores, quantities, Locale("es", "AR"))

    // Super Norte has cheapest arroz (1880) so should be cheapest overall with 10x weight
    assertEquals("Super Norte", sorted.first().storeName)
  }

  // --- buildQuantityByProduct edge cases ---

  @Test
  fun `empty list returns empty quantity map`() {
    val result = buildQuantityByProduct(emptyList(), Locale.US)

    assertEquals(emptyMap<String, Double>(), result)
  }

  @Test
  fun `single item returns correct quantity`() {
    val items = listOf(
      ListItemWithProduct(
        item = ListItemEntity(id = 1, listId = 1, productId = 10, quantity = 3.0, unit = "kg"),
        product = ProductEntity(id = 10, name = "Arroz", brand = null)
      )
    )

    val result = buildQuantityByProduct(items, Locale.US)

    assertEquals(mapOf("arroz" to 3.0), result)
  }

  @Test
  fun `duplicate products sum their quantities`() {
    val items = listOf(
      ListItemWithProduct(
        item = ListItemEntity(id = 1, listId = 1, productId = 10, quantity = 2.0, unit = "kg"),
        product = ProductEntity(id = 10, name = "Leche", brand = null)
      ),
      ListItemWithProduct(
        item = ListItemEntity(id = 2, listId = 1, productId = 11, quantity = 1.5, unit = "L"),
        product = ProductEntity(id = 11, name = "leche", brand = null)
      )
    )

    val result = buildQuantityByProduct(items, Locale.US)

    assertEquals(mapOf("leche" to 3.5), result)
  }

  @Test
  fun `storeTotalValue with empty quantities uses 1 per item`() {
    val store = demoStorePrices().first { it.storeName == "Walmart" }
    // Walmart: 1450 + 1020 + 1920 = 4390
    val total = storeTotalValue(store, emptyMap(), Locale.US)

    assertEquals(4390, total)
  }

  // --- Brand normalization ---

  @Test
  fun `displayBrand returns Sin marca for null`() {
    assertEquals("Sin marca", displayBrand(null))
  }

  @Test
  fun `displayBrand returns Sin marca for empty string`() {
    assertEquals("Sin marca", displayBrand(""))
  }

  @Test
  fun `displayBrand returns Sin marca for blank string`() {
    assertEquals("Sin marca", displayBrand("   "))
  }

  @Test
  fun `displayBrand returns the brand when not blank`() {
    assertEquals("La Serenísima", displayBrand("La Serenísima"))
  }
}

package com.compareprices.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.coroutineScope
import org.junit.Assert.assertEquals
import org.junit.Test

class DemoSeedDataTest {
  @Test
  fun `seeding is idempotent across concurrent calls`() = runBlocking {
    val store = FakeSeedStore()
    val transactionRunner = DemoSeedTransactionRunner { block -> block() }

    coroutineScope {
      val jobs = List(2) {
        launch(Dispatchers.Default) {
          seedDemoDataIfNeeded(
            transactionRunner = transactionRunner,
            shoppingListDao = store.shoppingListDao,
            productDao = store.productDao,
            listItemDao = store.listItemDao,
            priceSnapshotDao = store.priceSnapshotDao
          )
        }
      }
      jobs.joinAll()
    }

    assertEquals(1, store.shoppingLists.size)
    assertEquals(3, store.products.size)
    assertEquals(3, store.listItems.size)
  }

  @Test
  fun `seeding reuses existing product ids`() = runBlocking {
    val store = FakeSeedStore().apply {
      seedProduct(ProductEntity(id = 41, name = "Leche entera", brand = "La Serenisima", defaultUnit = "litro"))
      seedProduct(ProductEntity(id = 42, name = "Pan integral", brand = "Bimbo", defaultUnit = "unidad"))
      seedProduct(ProductEntity(id = 43, name = "Arroz largo fino", brand = "Gallo", defaultUnit = "kg"))
    }
    val transactionRunner = DemoSeedTransactionRunner { block -> block() }

    seedDemoDataIfNeeded(
      transactionRunner = transactionRunner,
      shoppingListDao = store.shoppingListDao,
      productDao = store.productDao,
      listItemDao = store.listItemDao,
      priceSnapshotDao = store.priceSnapshotDao
    )

    assertEquals(3, store.products.size)
    assertEquals(
      listOf(41L, 42L, 43L),
      store.listItems.map { it.productId }.sorted()
    )
  }

  @Test
  fun `normalizes brand values before insert`() {
    assertEquals("", normalizeBrand(null))
    assertEquals("", normalizeBrand("   "))
    assertEquals("Marca", normalizeBrand(" Marca "))
  }
}

private class FakeSeedStore {
  private val shoppingListRecords = mutableListOf<ShoppingListEntity>()
  private val productRecords = mutableListOf<ProductEntity>()
  private val listItemRecords = mutableListOf<ListItemEntity>()
  private val priceSnapshotRecords = mutableListOf<PriceSnapshotEntity>()

  val shoppingListDao: ShoppingListDao = object : ShoppingListDao {
    override suspend fun getAll(): List<ShoppingListEntity> = shoppingListRecords.toList()

    override suspend fun findByName(name: String): ShoppingListEntity? =
      shoppingListRecords.firstOrNull { it.name == name }

    override suspend fun insertIgnore(item: ShoppingListEntity): Long {
      delay(50)
      val nextId = (shoppingListRecords.maxOfOrNull { it.id } ?: 0) + 1
      shoppingListRecords.add(item.copy(id = nextId))
      return nextId
    }

    override suspend fun upsert(item: ShoppingListEntity): Long {
      shoppingListRecords.add(item)
      return item.id
    }

    override suspend fun count(): Int = shoppingListRecords.size

    override fun observeLatestList() = throw NotImplementedError("Not used in test")
  }

  val productDao: ProductDao = object : ProductDao {
    override suspend fun getAll(): List<ProductEntity> = productRecords.toList()

    override suspend fun getById(id: Long): ProductEntity? =
      productRecords.firstOrNull { it.id == id }

    override suspend fun findByKey(
      name: String,
      brand: String?,
      defaultUnit: String
    ): ProductEntity? {
      return productRecords.firstOrNull {
        it.name == name && it.brand == brand && it.defaultUnit == defaultUnit
      }
    }

    override suspend fun insertIgnore(items: List<ProductEntity>): List<Long> {
      return items.map { item ->
        val existing = findByKey(item.name, item.brand, item.defaultUnit)
        if (existing != null) {
          -1L
        } else {
          val nextId = (productRecords.maxOfOrNull { it.id } ?: 0) + 1
          productRecords.add(item.copy(id = nextId))
          nextId
        }
      }
    }

    override suspend fun upsertAll(items: List<ProductEntity>) {
      productRecords.addAll(items)
    }

    override suspend fun insertAll(items: List<ProductEntity>): List<Long> {
      val startId = (productRecords.maxOfOrNull { it.id } ?: 0) + 1
      val ids = items.indices.map { index -> startId + index }
      productRecords.addAll(items.mapIndexed { index, item -> item.copy(id = ids[index]) })
      return ids
    }

    override fun searchByName(query: String) =
      throw NotImplementedError("Not used in test")

    override suspend fun insert(item: ProductEntity): Long {
      val nextId = (productRecords.maxOfOrNull { it.id } ?: 0) + 1
      productRecords.add(item.copy(id = nextId))
      return nextId
    }
  }

  val listItemDao: ListItemDao = object : ListItemDao {
    override suspend fun itemsForList(listId: Long): List<ListItemEntity> =
      listItemRecords.filter { it.listId == listId }

    override suspend fun findItem(listId: Long, productId: Long): ListItemEntity? =
      listItemRecords.firstOrNull { it.listId == listId && it.productId == productId }

    override suspend fun upsert(item: ListItemEntity) {
      listItemRecords.removeAll { it.id == item.id }
      listItemRecords.add(item)
    }

    override suspend fun upsertAll(items: List<ListItemEntity>) {
      listItemRecords.addAll(items)
    }

    override suspend fun insert(item: ListItemEntity): Long {
      val nextId = (listItemRecords.maxOfOrNull { it.id } ?: 0) + 1
      listItemRecords.add(item.copy(id = nextId))
      return nextId
    }

    override suspend fun deleteById(itemId: Long) {
      listItemRecords.removeAll { it.id == itemId }
    }

    override suspend fun updateQuantity(itemId: Long, quantity: Double) {
      val existing = listItemRecords.firstOrNull { it.id == itemId } ?: return
      listItemRecords.removeAll { it.id == itemId }
      listItemRecords.add(existing.copy(quantity = quantity))
    }
  }

  val priceSnapshotDao: PriceSnapshotDao = object : PriceSnapshotDao {
    override suspend fun historyForProduct(productId: Long): List<PriceSnapshotEntity> =
      priceSnapshotRecords.filter { it.productId == productId }

    override suspend fun getHistoryForProduct(
      productId: Long,
      limit: Int
    ): List<PriceSnapshotEntity> =
      priceSnapshotRecords.filter { it.productId == productId }.take(limit)

    override suspend fun insertAll(items: List<PriceSnapshotEntity>) {
      priceSnapshotRecords.addAll(items)
    }
  }

  val shoppingLists: List<ShoppingListEntity>
    get() = shoppingListRecords.toList()

  val products: List<ProductEntity>
    get() = productRecords.toList()

  val listItems: List<ListItemEntity>
    get() = listItemRecords.toList()

  fun seedProduct(product: ProductEntity) {
    productRecords.add(product)
  }
}

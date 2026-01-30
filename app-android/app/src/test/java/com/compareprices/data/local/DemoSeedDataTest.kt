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
            listItemDao = store.listItemDao
          )
        }
      }
      jobs.joinAll()
    }

    assertEquals(1, store.shoppingLists.size)
    assertEquals(3, store.products.size)
    assertEquals(3, store.listItems.size)
  }
}

private class FakeSeedStore {
  private val shoppingListRecords = mutableListOf<ShoppingListEntity>()
  private val productRecords = mutableListOf<ProductEntity>()
  private val listItemRecords = mutableListOf<ListItemEntity>()

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

    override suspend fun upsertAll(items: List<ProductEntity>) {
      productRecords.addAll(items)
    }

    override suspend fun insertAll(items: List<ProductEntity>): List<Long> {
      val startId = (productRecords.maxOfOrNull { it.id } ?: 0) + 1
      val ids = items.indices.map { index -> startId + index }
      productRecords.addAll(items.mapIndexed { index, item -> item.copy(id = ids[index]) })
      return ids
    }
  }

  val listItemDao: ListItemDao = object : ListItemDao {
    override suspend fun itemsForList(listId: Long): List<ListItemEntity> =
      listItemRecords.filter { it.listId == listId }

    override suspend fun upsertAll(items: List<ListItemEntity>) {
      listItemRecords.addAll(items)
    }
  }

  val shoppingLists: List<ShoppingListEntity>
    get() = shoppingListRecords.toList()

  val products: List<ProductEntity>
    get() = productRecords.toList()

  val listItems: List<ListItemEntity>
    get() = listItemRecords.toList()

}

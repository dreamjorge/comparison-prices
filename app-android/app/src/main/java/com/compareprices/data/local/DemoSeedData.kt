package com.compareprices.data.local

import androidx.room.withTransaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val DEMO_LIST_NAME = "Compra semanal"
private val seedMutex = Mutex()

internal fun interface DemoSeedTransactionRunner {
  suspend fun runInTransaction(block: suspend () -> Unit)
}

internal fun normalizeBrand(brand: String?): String {
  return brand?.trim().orEmpty()
}

internal suspend fun seedDemoDataIfNeeded(
  database: AppDatabase,
  shoppingListDao: ShoppingListDao,
  productDao: ProductDao,
  listItemDao: ListItemDao
) {
  seedDemoDataIfNeeded(
    transactionRunner = DemoSeedTransactionRunner { block ->
      database.withTransaction { block() }
    },
    shoppingListDao = shoppingListDao,
    productDao = productDao,
    listItemDao = listItemDao
  )
}

internal suspend fun seedDemoDataIfNeeded(
  transactionRunner: DemoSeedTransactionRunner,
  shoppingListDao: ShoppingListDao,
  productDao: ProductDao,
  listItemDao: ListItemDao
) {
  seedMutex.withLock {
    transactionRunner.runInTransaction {
      val existingList = shoppingListDao.findByName(DEMO_LIST_NAME)
      if (existingList != null) {
        return@runInTransaction
      }

      val listId = shoppingListDao.insertIgnore(
        ShoppingListEntity(name = DEMO_LIST_NAME, createdAt = System.currentTimeMillis())
      )
      if (listId == -1L) {
        return@runInTransaction
      }

      val products = listOf(
        ProductEntity(name = "Leche entera", brand = "La Serenisima", defaultUnit = "litro"),
        ProductEntity(name = "Pan integral", brand = "Bimbo", defaultUnit = "unidad"),
        ProductEntity(name = "Arroz largo fino", brand = "Gallo", defaultUnit = "kg")
      )
      val normalizedProducts = products.map { product ->
        product.copy(brand = normalizeBrand(product.brand))
      }
      val insertResults = productDao.insertIgnore(normalizedProducts)
      val productIds = normalizedProducts.mapIndexed { index, product ->
        val insertedId = insertResults.getOrNull(index) ?: -1L
        if (insertedId != -1L) {
          insertedId
        } else {
          productDao.findByKey(product.name, product.brand, product.defaultUnit)?.id ?: 0
        }
      }

      val items = listOf(
        ListItemEntity(
          listId = listId,
          productId = productIds.getOrElse(0) { 0 },
          quantity = 2.0,
          unit = "L"
        ),
        ListItemEntity(
          listId = listId,
          productId = productIds.getOrElse(1) { 0 },
          quantity = 1.0,
          unit = "unidad"
        ),
        ListItemEntity(
          listId = listId,
          productId = productIds.getOrElse(2) { 0 },
          quantity = 1.0,
          unit = "kg"
        )
      )
      listItemDao.upsertAll(items)
    }
  }
}

package com.compareprices.data.local

internal suspend fun seedDemoDataIfNeeded(
  shoppingListDao: ShoppingListDao,
  productDao: ProductDao,
  listItemDao: ListItemDao
) {
  if (shoppingListDao.count() > 0) {
    return
  }

  val products = listOf(
    ProductEntity(name = "Leche entera", brand = "La Serenisima", defaultUnit = "litro"),
    ProductEntity(name = "Pan integral", brand = "Bimbo", defaultUnit = "unidad"),
    ProductEntity(name = "Arroz largo fino", brand = "Gallo", defaultUnit = "kg")
  )
  val productIds = productDao.insertAll(products)
  val listId = shoppingListDao.upsert(
    ShoppingListEntity(name = "Compra semanal", createdAt = System.currentTimeMillis())
  )

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

package com.compareprices.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [
    ProductEntity::class,
    StoreEntity::class,
    PriceSnapshotEntity::class,
    ShoppingListEntity::class,
    ListItemEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun storeDao(): StoreDao
  abstract fun priceSnapshotDao(): PriceSnapshotDao
  abstract fun shoppingListDao(): ShoppingListDao
  abstract fun listItemDao(): ListItemDao
}

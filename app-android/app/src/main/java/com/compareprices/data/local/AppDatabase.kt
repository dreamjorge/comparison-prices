package com.compareprices.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
  entities = [
    ProductEntity::class,
    StoreEntity::class,
    PriceSnapshotEntity::class,
    ShoppingListEntity::class,
    ListItemEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun storeDao(): StoreDao
  abstract fun priceSnapshotDao(): PriceSnapshotDao
  abstract fun shoppingListDao(): ShoppingListDao
  abstract fun listItemDao(): ListItemDao

  companion object {
    val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """
          DELETE FROM shopping_lists
          WHERE name IN (SELECT name FROM shopping_lists GROUP BY name HAVING COUNT(*) > 1)
          AND id NOT IN (SELECT MAX(id) FROM shopping_lists GROUP BY name)
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE UNIQUE INDEX IF NOT EXISTS index_shopping_lists_name
          ON shopping_lists(name)
          """.trimIndent()
        )
      }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """
          UPDATE list_items
          SET productId = (
            SELECT MIN(p2.id)
            FROM products p
            JOIN products p2
              ON p2.name = p.name
              AND p2.brand IS p.brand
              AND p2.defaultUnit = p.defaultUnit
            WHERE p.id = list_items.productId
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          UPDATE price_snapshots
          SET productId = (
            SELECT MIN(p2.id)
            FROM products p
            JOIN products p2
              ON p2.name = p.name
              AND p2.brand IS p.brand
              AND p2.defaultUnit = p.defaultUnit
            WHERE p.id = price_snapshots.productId
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          DELETE FROM list_items
          WHERE id NOT IN (
            SELECT MIN(id)
            FROM list_items
            GROUP BY listId, productId
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          DELETE FROM products
          WHERE id NOT IN (
            SELECT MIN(id)
            FROM products
            GROUP BY name, brand, defaultUnit
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE UNIQUE INDEX IF NOT EXISTS index_products_name_brand_defaultUnit
          ON products(name, brand, defaultUnit)
          """.trimIndent()
        )
      }
    }
  }
}

package com.compareprices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY name ASC")
  suspend fun getAll(): List<ProductEntity>

  @Query(
    """
    SELECT * FROM products
    WHERE name = :name
      AND ((brand IS NULL AND :brand IS NULL) OR brand = :brand)
      AND defaultUnit = :defaultUnit
    LIMIT 1
    """
  )
  suspend fun findByKey(name: String, brand: String?, defaultUnit: String): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertIgnore(items: List<ProductEntity>): List<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<ProductEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<ProductEntity>): List<Long>
}

@Dao
interface StoreDao {
  @Query("SELECT * FROM stores ORDER BY name ASC")
  suspend fun getAll(): List<StoreEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<StoreEntity>)
}

@Dao
interface PriceSnapshotDao {
  @Query("SELECT * FROM price_snapshots WHERE productId = :productId ORDER BY capturedAt DESC")
  suspend fun historyForProduct(productId: Long): List<PriceSnapshotEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<PriceSnapshotEntity>)
}

@Dao
interface ShoppingListDao {
  @Query("SELECT * FROM shopping_lists ORDER BY createdAt DESC")
  suspend fun getAll(): List<ShoppingListEntity>

  @Query("SELECT * FROM shopping_lists WHERE name = :name LIMIT 1")
  suspend fun findByName(name: String): ShoppingListEntity?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertIgnore(item: ShoppingListEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: ShoppingListEntity): Long

  @Query("SELECT COUNT(*) FROM shopping_lists")
  suspend fun count(): Int

  @Transaction
  @Query("SELECT * FROM shopping_lists ORDER BY createdAt DESC LIMIT 1")
  fun observeLatestList(): Flow<ShoppingListWithItems?>
}

@Dao
interface ListItemDao {
  @Query("SELECT * FROM list_items WHERE listId = :listId")
  suspend fun itemsForList(listId: Long): List<ListItemEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<ListItemEntity>)
}

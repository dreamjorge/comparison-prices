package com.compareprices.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY name ASC")
  suspend fun getAll(): List<ProductEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<ProductEntity>)
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

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: ShoppingListEntity): Long
}

@Dao
interface ListItemDao {
  @Query("SELECT * FROM list_items WHERE listId = :listId")
  suspend fun itemsForList(listId: Long): List<ListItemEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<ListItemEntity>)
}
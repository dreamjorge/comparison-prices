package com.compareprices.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val brand: String? = null,
  val defaultUnit: String = "unit"
)

@Entity(tableName = "stores")
data class StoreEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val zone: String? = null
)

@Entity(
  tableName = "price_snapshots",
  indices = [
    Index(value = ["productId", "storeId"]),
    Index(value = ["capturedAt"])
  ]
)
data class PriceSnapshotEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val storeId: Long,
  val price: Double,
  val currency: String = "USD",
  val capturedAt: Long
)

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val createdAt: Long
)

@Entity(
  tableName = "list_items",
  indices = [Index(value = ["listId", "productId"], unique = true)]
)
data class ListItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val listId: Long,
  val productId: Long,
  val quantity: Double,
  val unit: String
)

data class ListItemWithProduct(
  @Embedded val item: ListItemEntity,
  @Relation(parentColumn = "productId", entityColumn = "id")
  val product: ProductEntity
)

data class ShoppingListWithItems(
  @Embedded val list: ShoppingListEntity,
  @Relation(
    entity = ListItemEntity::class,
    parentColumn = "id",
    entityColumn = "listId"
  )
  val items: List<ListItemWithProduct>
)

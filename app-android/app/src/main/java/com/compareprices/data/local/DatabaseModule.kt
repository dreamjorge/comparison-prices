package com.compareprices.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "compare_prices.db")
      .addMigrations(
        AppDatabase.MIGRATION_1_2,
        AppDatabase.MIGRATION_2_3,
        AppDatabase.MIGRATION_3_4
      )
      .build()
  }

  @Provides
  fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

  @Provides
  fun provideStoreDao(db: AppDatabase): StoreDao = db.storeDao()

  @Provides
  fun providePriceSnapshotDao(db: AppDatabase): PriceSnapshotDao = db.priceSnapshotDao()

  @Provides
  fun provideShoppingListDao(db: AppDatabase): ShoppingListDao = db.shoppingListDao()

  @Provides
  fun provideListItemDao(db: AppDatabase): ListItemDao = db.listItemDao()
}

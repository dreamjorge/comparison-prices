package com.compareprices.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.hilt.work.HiltWorker
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.utils.showPriceDropNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PriceRefreshWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao,
  private val priceSnapshotDao: PriceSnapshotDao
) : CoroutineWorker(appContext, params) {

  override suspend fun doWork(): Result {
    // Refresh local price snapshots and post local alerts.
    showPriceDropNotification(
        applicationContext,
        "Producto Demo",
        15
    )
    return Result.success()
  }
}
package com.compareprices.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ListItemWithProduct
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.repository.UserPreferencesRepository
import com.compareprices.ui.compare.buildQuantityByProduct
import com.compareprices.ui.compare.demoStorePrices
import com.compareprices.ui.compare.sortStorePricesByTotal
import com.compareprices.ui.compare.storeTotalValue
import com.compareprices.ui.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Locale

@HiltWorker
class PriceRefreshWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted params: WorkerParameters,
  private val notificationHelper: NotificationHelper,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao,
  private val priceSnapshotDao: PriceSnapshotDao,
  private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    // TICKET 7.4: Clear expired rewarded access on background refresh
    userPreferencesRepository.clearExpiredRewards()

    // TICKET 5.2: Compare cheapest store and notify if changed
    val listWithItems = shoppingListDao.observeLatestList().first()
    val listItems: List<ListItemWithProduct> = listWithItems?.items.orEmpty()
    val locale = Locale.getDefault()
    val quantityByProduct = buildQuantityByProduct(listItems, locale)
    val storePrices = demoStorePrices()
    val sortedStores = sortStorePricesByTotal(storePrices, quantityByProduct, locale)
    val cheapestStore = sortedStores.firstOrNull()

    if (cheapestStore != null) {
      val previousCheapest = userPreferencesRepository.getLastCheapestStore().first()
      if (previousCheapest != null && previousCheapest != cheapestStore.storeName) {
        val secondStore = sortedStores.getOrNull(1)
        val savings = if (secondStore != null) {
          storeTotalValue(secondStore, quantityByProduct, locale) -
            storeTotalValue(cheapestStore, quantityByProduct, locale)
        } else {
          0
        }
        notificationHelper.showBestStoreChangedNotification(cheapestStore.storeName, savings)
      }
      userPreferencesRepository.setLastCheapestStore(cheapestStore.storeName)
    }

    return Result.success()
  }
}

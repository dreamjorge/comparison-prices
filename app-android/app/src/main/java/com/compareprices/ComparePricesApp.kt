package com.compareprices

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.compareprices.data.local.AppDatabase
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.local.seedDemoDataIfNeeded
import com.compareprices.ui.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class ComparePricesApp : Application(), Configuration.Provider {
  
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun onCreate() {
    super.onCreate()
    NotificationHelper.createNotificationChannel(this)
  }
}
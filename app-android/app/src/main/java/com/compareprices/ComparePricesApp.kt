package com.compareprices

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.compareprices.utils.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ComparePricesApp : Application(), Configuration.Provider {
  
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel(this)
  }
}
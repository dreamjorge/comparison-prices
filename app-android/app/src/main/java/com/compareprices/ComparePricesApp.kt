package com.compareprices

import android.app.Application
import com.compareprices.ui.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ComparePricesApp : Application() {
  override fun onCreate() {
    super.onCreate()
    NotificationHelper.createNotificationChannel(this)
  }
}
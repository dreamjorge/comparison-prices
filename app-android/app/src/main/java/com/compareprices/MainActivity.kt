package com.compareprices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import com.compareprices.ui.AppRoot
import com.compareprices.ui.theme.ComparePricesTheme
import com.compareprices.data.work.PriceRefreshWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    schedulePriceRefresh()

    setContent {
      ComparePricesTheme {
        AppRoot()
      }
    }
  }

  private fun schedulePriceRefresh() {
    val workRequest = PeriodicWorkRequestBuilder<PriceRefreshWorker>(8, TimeUnit.HOURS)
      .setInitialDelay(15, TimeUnit.MINUTES)
      .build()

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
      "PriceRefreshWork",
      ExistingPeriodicWorkPolicy.KEEP,
      workRequest
    )
  }
}
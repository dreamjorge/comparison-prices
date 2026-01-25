package com.compareprices.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PriceRefreshWorker(
  appContext: Context,
  params: WorkerParameters
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    // TODO: refresh local price snapshots and post local alerts.
    return Result.success()
  }
}
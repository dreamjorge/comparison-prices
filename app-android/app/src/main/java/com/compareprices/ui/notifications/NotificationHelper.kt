package com.compareprices.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationHelper(private val context: Context) {
  fun notifyPriceDrop(productName: String, priceLabel: String) {
    if (!hasNotificationPermission()) {
      return
    }

    ensureChannel()

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("Precio más bajo disponible")
      .setContentText("$productName ahora a $priceLabel")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)

    try {
      NotificationManagerCompat.from(context).notify(productName.hashCode(), builder.build())
    } catch (_: SecurityException) {
      // Permission could be revoked while the app is running.
    }
  }

  private fun hasNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return true
    }

    return ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun ensureChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (manager.getNotificationChannel(CHANNEL_ID) != null) {
      return
    }

    val channel = NotificationChannel(
      CHANNEL_ID,
      CHANNEL_NAME,
      NotificationManager.IMPORTANCE_DEFAULT
    )
    manager.createNotificationChannel(channel)
  }

  companion object {
    private const val CHANNEL_ID = "price_alerts"
    private const val CHANNEL_NAME = "Alertas de precios"

    /**
     * Static helper to create notification channel (for app initialization).
     * Called from ComparePricesApp.onCreate()
     */
    fun createNotificationChannel(context: Context) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
      }

      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (manager.getNotificationChannel(CHANNEL_ID) != null) {
        return
      }

      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      )
      manager.createNotificationChannel(channel)
    }

    /**
     * Static helper for price drop notifications (for WorkManager).
     * Called from PriceRefreshWorker.
     * 
     * Note: This doesn't check permissions - ensure caller has permission.
     */
    fun showPriceDropNotification(context: Context, productName: String, dropPercentage: Int) {
      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Baja de Precio Detectada!")
        .setContentText("El producto $productName ha bajado un $dropPercentage%.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

      try {
        NotificationManagerCompat.from(context).notify(productName.hashCode(), builder.build())
      } catch (_: SecurityException) {
        // Permission could be revoked while the app is running.
      }
    }
  }
}

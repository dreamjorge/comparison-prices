package com.compareprices.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.compareprices.MainActivity
import com.compareprices.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
  @ApplicationContext private val context: Context
) {
  companion object {
    const val CHANNEL_ID = "price_alerts"
    const val CHANNEL_NAME = "Alertas de Precios"
    const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
          description = "Notificaciones cuando los precios bajan"
          enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
      }
    }
  }

  fun showPriceDropNotification(
    productName: String,
    storeName: String,
    oldPrice: Double,
    newPrice: Double
  ) {
    if (!hasNotificationPermission()) return

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE
    )

    val savings = oldPrice - newPrice
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("¡Precio más bajo!")
      .setContentText("$productName en $storeName: ahorra $${String.format("%.2f", savings)}")
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText("$productName bajó de $${String.format("%.2f", oldPrice)} a $${String.format("%.2f", newPrice)} en $storeName")
      )
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    NotificationManagerCompat.from(context).notify(
      NOTIFICATION_ID_BASE + productName.hashCode(),
      notification
    )
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
}

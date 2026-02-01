package com.compareprices.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.compareprices.R

object NotificationHelper {
    private const val CHANNEL_ID = "price_alerts"
    private const val CHANNEL_NAME = "Alertas de Precios"
    private const val CHANNEL_DESC = "Notificaciones sobre bajadas de precios en tu lista"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPriceDropNotification(context: Context, productName: String, dropPercentage: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Simplified icon
            .setContentTitle("Baja de Precio Detectada!")
            .setContentText("El producto $productName ha bajado un $dropPercentage%.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // Check permission in real app, here we assume it's granted or for demo
            notify(productName.hashCode(), builder.build())
        }
    }

    fun showCheapestStoreNotification(context: Context, listName: String, storeName: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva tienda más barata")
            .setContentText("La tienda $storeName es ahora la opción más barata para $listName.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(listName.hashCode(), builder.build())
        }
    }
}

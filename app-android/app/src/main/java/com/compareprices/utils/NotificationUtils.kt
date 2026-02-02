package com.compareprices.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "price_drop_channel"
private const val CHANNEL_NAME = "Price Drop Alerts"
private const val NOTIFICATION_ID = 1

fun createNotificationChannel(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
}

fun showPriceDropNotification(context: Context, productName: String, price: Int) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        // Use a framework icon so notifications compile even when no app drawable exists yet.
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Price Drop Alert!")
        .setContentText("The price of $productName has dropped to \$$price")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    notificationManager.notify(NOTIFICATION_ID, notification)
}

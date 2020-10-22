package com.showdy.alarmclock.util

import android.app.*
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by <b>Showdy</b> on 2020/10/17 20:23
 *
 *  通知构建类
 */

const val CHANNEL_ID = "com.gyenno.watch.channel"
const val CHANNEL_NAME = "WatchChannel"
const val CHANNEL_DESCRIPTION = "This is a watch sensor channel"

val atomicNotifyId = AtomicInteger(0x001)

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setShowBadge(true)
            setBypassDnd(true)
            //闪光灯
            lightColor = Color.RED
            enableLights(true)
            //锁屏显示通知
            lockscreenVisibility = Notification.VISIBILITY_SECRET
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 100, 200)
            setSound(
                Settings.System.DEFAULT_NOTIFICATION_URI,
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )
        }
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}


fun Context.createNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String
): Notification {
    //创建channel
    createNotificationChannel()
    //创建通知
    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(iconResId)
        .setContentText(contentText)
        .setContentTitle(contentTitle)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
        .setAutoCancel(true)
        .build()
}

/**
 * 发送普通通知
 */
fun Context.sendNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String
): Int {

    val notification =
        createNotification(pendingIntent, iconResId, contentText, contentTitle)
    val notificationId = atomicNotifyId.getAndIncrement()
    with(NotificationManagerCompat.from(this)) {
        notify(notificationId, notification)
    }
    return notificationId
}

/**
 * Android O 发送前台通知
 */
fun Service.sendForegroundNotification(
    pendingIntent: PendingIntent?,
    iconResId: Int,
    contentText: String,
    contentTitle: String
): Int {
    createNotificationChannel()
    val notificationId = atomicNotifyId.getAndIncrement()
    val notification =
        createNotification(pendingIntent, iconResId, contentText, contentTitle)
    startForeground(notificationId, notification)
    return notificationId
}


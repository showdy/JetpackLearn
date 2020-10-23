package com.showdy.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.showdy.alarm.App
import com.showdy.alarm.R
import com.showdy.alarm.activity.StatusListActivity
import com.showdy.alarm.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/14 21:00
 *
 * 接受到广播后，弹出dialog或者通知栏
 *
 * Android7.0以后，静态注册广播可能会收不到
 */
class ClockBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receiver", "onReceive: ${intent?.action}--${App.isForeground}")
        when (App.isForeground) {
            false -> StatusListActivity.start(context!!)
            true -> showClockInNotification(context!!)
        }
        AlarmSetting.instance.setIntervalExactAlarm()
    }

    private fun showClockInNotification(context: Context) {
        val pendingIntent = PendingIntentFactory.getStatusClockPendingIntent(context)
        context.sendNotification(
            pendingIntent = pendingIntent,
            iconResId = R.mipmap.gyenno_splash,
            contentText = "It's time to record your health status",
            contentTitle = "Time to Clock",
            cancelable = true
        )
        context.sound()
        context.vibrate()
    }
}
package com.showdy.alarmclock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.showdy.alarmclock.App
import com.showdy.alarmclock.R
import com.showdy.alarmclock.activity.StatusListActivity
import com.showdy.alarmclock.util.AlarmSetting
import com.showdy.alarmclock.util.PendingIntentFactory
import com.showdy.alarmclock.util.sendNotification
import java.time.LocalTime

/**
 * Created by <b>Showdy</b> on 2020/10/14 21:00
 *
 * 接受到广播后，弹出dialog或者通知栏
 *
 * Android7.0以后，静态注册广播可能会收不到
 */
class ClockBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receiver", "onReceive: ${intent?.action}--${App.isForeground}")
        when (App.isForeground) {
            true -> StatusListActivity.start(context!!)
            false -> showClockInNotification(context!!)
        }

        Log.d("TAG", "onReceive: ${LocalTime.now()}")
        //设置下一次的闹钟
        AlarmSetting.instance.setIntervalExactAlarm()
    }

    private fun showClockInNotification(context: Context) {
        val pendingIntent = PendingIntentFactory.getStatusClockPendingIntent(context)
        context.sendNotification(
            pendingIntent = pendingIntent,
            iconResId = R.mipmap.ic_launcher,
            contentText = "It's time to record your health status",
            contentTitle = "Time to Clock"
        )
    }
}
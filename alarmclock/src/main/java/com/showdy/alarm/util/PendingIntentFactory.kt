package com.showdy.alarm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.showdy.alarm.activity.StatusListActivity
import com.showdy.alarm.receiver.ClockBroadcastReceiver

/**
 * Created by <b>Showdy</b> on 2020/10/21 14:24
 *
 */
object PendingIntentFactory {


    fun getStatusClockPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, StatusListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            StatusListActivity.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * 启动一个广播
     */
    fun getBroadcastPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ClockBroadcastReceiver::class.java).apply {
            action = "com.gyenno.watch.clock"
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
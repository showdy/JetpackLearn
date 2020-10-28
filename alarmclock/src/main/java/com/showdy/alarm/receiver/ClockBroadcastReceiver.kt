package com.showdy.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.showdy.alarm.App
import com.showdy.alarm.R
import com.showdy.alarm.activity.StatusListActivity
import com.showdy.alarm.provider.context
import com.showdy.alarm.util.*
import com.orhanobut.logger.Logger

/**
 * Created by <b>Showdy</b> on 2020/10/14 21:00
 *
 * 接受到广播后，弹出dialog或者通知栏
 *
 * Android7.0以后，静态注册广播可能会收不到
 */
class ClockBroadcastReceiver : BroadcastReceiver() {

    companion object {

        private val instance by lazy { ClockBroadcastReceiver() }

        fun registerClockReceiver() {
            //注册广播
            LocalBroadcastManager.getInstance(context).registerReceiver(
                instance, IntentFilter("com.gyenno.watch.clock")
            )
        }

        fun unregisterClockReceiver() {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(instance)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.t("TAG").d("ClockBroadcastReceiver: ${intent?.action}--${App.isForeground}")
        when (App.isForeground) {
            false -> StatusListActivity.start(context!!)
            true -> showClockInNotification(context!!)
        }
        AlarmSetting.instance.setExactAlarm()
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
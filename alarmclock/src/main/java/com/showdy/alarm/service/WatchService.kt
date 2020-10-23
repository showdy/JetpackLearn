package com.showdy.alarm.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.showdy.alarm.R
import com.showdy.alarm.util.sendForegroundNotification
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * Created by <b>Showdy</b> on 2020/10/14 17:07
 *
 *  进行sensor数据采集和打卡的service
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class WatchService : LifecycleService() {

    companion object {
        fun startService(context: Context, intent: Intent) {
            intent.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                } else {
                    context.startService(it)
                }
            }
        }

        fun stopService(context: Context, intent: Intent) {
            context.stopService(intent)
        }
    }


    override fun onCreate() {
        super.onCreate()
//        lifecycle.addObserver(SensorObserver(this, lifecycleScope))
        lifecycle.addObserver(ClockObserver(this))
        //启动前台通知
        sendForegroundNotification(
            pendingIntent = null,
            iconResId = R.mipmap.gyenno_splash,
            contentText = "ForegroundService",
            contentTitle = "Service Running..."
        )
    }
}
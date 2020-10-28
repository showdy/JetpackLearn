package com.showdy.alarm.service

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.showdy.alarm.R
import com.showdy.alarm.provider.context
import com.showdy.alarm.receiver.BatteryChargeReceiver
import com.showdy.alarm.service.observer.ClockObserver
import com.showdy.alarm.util.sendForegroundNotification
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * Created by <b>Showdy</b> on 2020/10/14 17:07
 *
 *  进行sensor数据采集和打卡的service
 */

class WatchService : LifecycleService() {

    companion object {

        private val intent by lazy { Intent(context, WatchService::class.java) }

        fun startService() {
            intent.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                } else {
                    context.startService(it)
                }
            }
        }

        fun stopService() {
            context.stopService(intent)
        }
    }


    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        Logger.t("TAG").d("onCreate: WatchService")
        Log.d("TAG","onCreate: WatchService")
        lifecycle.addObserver(ClockObserver())
        //启动前台通知
        sendForegroundNotification(
            pendingIntent = null,
            iconResId = R.mipmap.gyenno_splash,
            contentText = "ForegroundService",
            contentTitle = "Service Running..."
        )
        //注册广播
        BatteryChargeReceiver.registerBatteryReceiver()

        //启动sensorService
        SensorService.startService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.t("TAG").d("onStartCommand: WatchService")
        Log.d("TAG", "onStartCommand: WatchService")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        Logger.t("TAG").d("onDestroy: WatchService")
        Log.d("TAG","onDestroy: WatchService")
        BatteryChargeReceiver.unregisterBatteryReceiver()
        SensorService.stopService()
        super.onDestroy()
    }
}
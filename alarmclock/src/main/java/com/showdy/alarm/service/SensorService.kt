package com.showdy.alarm.service

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.showdy.alarm.R
import com.showdy.alarm.provider.context
import com.showdy.alarm.service.observer.SensorAllObserver
import com.showdy.alarm.util.sendForegroundNotification
import com.orhanobut.logger.Logger

/**
 * Created by <b>Showdy</b> on 2020/10/24 15:07
 *
 * 传感器数据采集服务，根据电池是否在充电进行开启和关闭
 */
class SensorService : LifecycleService() {


    companion object {

        private val intent: Intent by lazy { Intent(context, SensorService::class.java) }

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


    override fun onCreate() {
        super.onCreate()
        Logger.t("TAG").d("onCreate: SensorService")
        Log.d("TAG","onCreate: SensorService")
        //开始采集数据
        lifecycle.addObserver(SensorAllObserver(this, lifecycleScope))

        //启动前台通知
        sendForegroundNotification(
            pendingIntent = null,
            iconResId = R.mipmap.gyenno_splash,
            contentText = "SensorService",
            contentTitle = "Sensor Collecting..."
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.t("TAG").d("onStartCommand: SensorService")
        Log.d("TAG","onStartCommand: SensorService")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        Logger.t("TAG").d("onDestroy: SensorService")
        Log.d("TAG","onDestroy: SensorService")
    }

}
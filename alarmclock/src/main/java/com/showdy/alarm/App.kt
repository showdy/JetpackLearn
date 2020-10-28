package com.showdy.alarm

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ProcessLifecycleOwner
import com.showdy.alarm.receiver.ClockBroadcastReceiver
import com.showdy.alarm.service.WatchService
import com.showdy.alarm.util.log.CustomCSVFormatStrategy
import com.orhanobut.logger.*
import kotlin.properties.Delegates

/**
 * Created by <b>Showdy</b> on 2020/10/14 19:43
 *
 *  由于TicWatchPro是Android-9 ，需要适配版本
 *
 *  服务和广播的启动和注册是想尽量保证传感器服务存活时间够长，获取更多的数据
 */
class App : Application() {

    companion object {
        var instance: App by Delegates.notNull()

        val isForeground
            get() = AppObserver.activityCount > 0
    }

    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //绑定生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppObserver())

        //注册服务和广播
        registerServiceAndReceiver()

        //初始化Logger
        initLogger()

    }

    private fun initLogger() {
        val formatStrategy = CustomCSVFormatStrategy.newBuilder()
            .build()
        Logger.addLogAdapter(DiskLogAdapter(formatStrategy))
    }

    private fun registerServiceAndReceiver() {
        //启动前台服务
        WatchService.startService()
        //注册广播
        ClockBroadcastReceiver.registerClockReceiver()
    }

}
package com.showdy.alarmclock

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.showdy.alarmclock.receiver.ClockBroadcastReceiver
import com.showdy.alarmclock.service.WatchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlin.properties.Delegates

/**
 * Created by <b>Showdy</b> on 2020/10/14 19:43
 *
 *  由于TicWatchPro是Android-9 ，需要适配版本
 *
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class App : Application() {

    companion object {
        var instance: App by Delegates.notNull()

        val isForeground
            get() = AppObserver.activityCount > 0
    }

    private val serviceIntent: Intent by lazy { Intent(this, WatchService::class.java) }

    private val receiver: BroadcastReceiver by lazy { ClockBroadcastReceiver() }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //绑定生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppObserver())

        //启动前台服务
        WatchService.startService(this, serviceIntent)

        //注册广播
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter("com.gyenno.watch.clock")
        )
    }


}
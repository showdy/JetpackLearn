package com.showdy.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.showdy.alarm.provider.context
import com.showdy.alarm.service.SensorService
import com.orhanobut.logger.Logger

/**
 * Created by <b>Showdy</b> on 2020/10/24 14:49
 *
 *
 * 用来接收系统发送与电池有关的广播，
 *
 * @see  <action android:name="android.intent.action.ACTION_BATTERY_CHARGED"/>
 * @see  <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
 * @see  <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
 *
 */
class BatteryChargeReceiver : BroadcastReceiver() {

    companion object {

        val instance: BatteryChargeReceiver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BatteryChargeReceiver() }

        fun registerBatteryReceiver() {
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            context.registerReceiver(instance, intentFilter)
        }

        fun unregisterBatteryReceiver() {
            context.unregisterReceiver(instance)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.t("TAG").d("BatteryChargeReceiver:onReceive: ${intent?.action}")
        Log.d("TAG", "BatteryChargeReceiver: onReceive--${intent?.action}")
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                //暂停传感器采集服务
                SensorService.stopService()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                SensorService.startService()
            }
            /**
             * 拔出充电器，先收到ACTION_POWER_DISCONNECTED,又会收到BATTERY_CHANGED,
             * 由于广播的先后顺序不一致，导致不能监听BATTERY电池电量变化的广播
             *
             * 即，在充电的时候打开app，服务还是会运行，不去停止，否则当拔掉充电器的时候，
             * 服务会起不来。以此做个兼容考虑
             */
            Intent.ACTION_BATTERY_CHANGED->{

            }
        }
    }
}
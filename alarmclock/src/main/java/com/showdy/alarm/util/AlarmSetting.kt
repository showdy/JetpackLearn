package com.showdy.alarm.util

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.showdy.alarm.App
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by <b>Showdy</b> on 2020/10/16 17:21
 *
 * 闹钟设置
 */
class AlarmSetting private constructor() {

    companion object {

        const val TIME_INTERVAL = 30 * 60 * 1000

        //DCL
        val instance: AlarmSetting by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AlarmSetting()
        }
    }

    private val atomicCounter = AtomicInteger(0)

    private val alarmManager by lazy {
        App.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val pendingIntent by lazy {
        PendingIntentFactory.getBroadcastPendingIntent(App.instance)
    }


    /**
     * 如果设置的起始时间已经过去，APP会立即受到广播
     */
    fun setExactAlarm() {
        val calendar = findNextHalfHourCalendar()
        Log.d("TAG", "alarm:${calendar.time.format()}")
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun setIntervalExactAlarm() {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + TIME_INTERVAL,
            pendingIntent
        )
    }

    /**
     *
     * 寻找最近的可设置闹钟的整点
     *
     */
    private fun findNextHalfHourCalendar(): Calendar {
        val calendar = Calendar.getInstance(Locale.CHINA)
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        when {
            hour >= 20 -> { //设置早上八点
                calendar.set(Calendar.DAY_OF_YEAR, day + 1)
                calendar.set(Calendar.HOUR_OF_DAY, 8)
                calendar.set(Calendar.MINUTE, 0)
            }
            hour <= 8 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 8)
                calendar.set(Calendar.MINUTE, 0)
            }
            minute in 0..29 -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, 30)
            }
            minute in 30..59 -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour + 1)
                calendar.set(Calendar.MINUTE, 0)
            }
        }
        return calendar
    }


    /**
     * 早上八点开始，每三十分提醒一次
     */
    fun setRepeatingAlarm(hour: Int, minute: Int, interval: Long) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * interval,
            pendingIntent
        )
    }

    /**
     * 取消闹钟
     */
    fun cancel() {
        alarmManager.cancel(pendingIntent)
    }


    fun cancelAtTime(hour: Int, minute: Int) {

        //重置索引
        resetAtomicCounter()

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        if (Calendar.getInstance().time == calendar) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun resetAtomicCounter() {
        atomicCounter.set(0)
    }
}
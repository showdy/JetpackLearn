package com.showdy.alarmclock.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarmclock.util.AlarmSetting

/**
 * Created by <b>Showdy</b> on 2020/10/16 15:08
 *
 *  打卡
 */
class ClockObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)


        Log.d("ClockObserver", "onCreate: create alarm at 8:00")

        AlarmSetting.instance.setExactAlarm()
    }
}
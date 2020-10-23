package com.showdy.alarm.service

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarm.util.AlarmSetting

/**
 * Created by <b>Showdy</b> on 2020/10/16 15:08
 *
 *  打卡
 */
class ClockObserver(private val context: Context) : DefaultLifecycleObserver {


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        //定下闹钟
        AlarmSetting.instance.setExactAlarm()

    }
}
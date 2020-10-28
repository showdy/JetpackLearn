package com.showdy.alarm.service.observer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarm.util.AlarmSetting

/**
 * Created by <b>Showdy</b> on 2020/10/16 15:08
 *
 *  设定闹钟定时打卡
 */
class ClockObserver : DefaultLifecycleObserver {


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        //定下闹钟
        AlarmSetting.instance.setExactAlarm()

    }
}
package com.showdy.alarmclock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.showdy.alarmclock.util.AlarmSetting

/**
 * Created by <b>Showdy</b> on 2020/10/16 17:16
 *
 * 设备重启会清除闹钟,所以要重新设置闹钟
 */
class RebootCompleteReceiver : BroadcastReceiver() {

    private val alarmSetting by lazy { AlarmSetting.instance }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            //设置一个精确的定时任务
            //设置闹钟的起始时间 8:00，结束闹钟20:00

//            clockAlarm = AlarmSetting(context!!)
//
//            //晚上八点还有收到就取消广播
//            clockAlarm.cancelAtTime(20, 30)
//            //八点开始，每三十分执行一次闹钟
//            clockAlarm.setRepeatingAlarm(8, 0,30)
        }
    }


}
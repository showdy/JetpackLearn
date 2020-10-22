package com.showdy.alarmclock

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Created by <b>Showdy</b> on 2020/10/15 20:11
 *
 */
class AppObserver : DefaultLifecycleObserver {

    companion object {
        const val TAG = "AppObserver"

        @JvmField
        var activityCount = 0
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        activityCount++
        Log.d(TAG, "onCreate: $owner--$activityCount")

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        activityCount--
        Log.d(TAG, "onDestroy: $owner--$activityCount")

    }
}
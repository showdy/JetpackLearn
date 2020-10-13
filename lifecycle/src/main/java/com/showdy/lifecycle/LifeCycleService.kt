package com.showdy.lifecycle

import androidx.lifecycle.LifecycleService

/**
 * Created by <b>Showdy</b> on 2020/10/12 19:32
 *
 * 实现对service生命周期的观察
 */
class LifeCycleService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(ServiceLifecycleObserver())
    }
}
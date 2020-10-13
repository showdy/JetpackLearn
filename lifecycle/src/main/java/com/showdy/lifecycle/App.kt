package com.showdy.lifecycle

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Created by <b>Showdy</b> on 2020/10/12 19:23
 *
 */
class App : Application() {


    private val processLifecycleObserver by lazy { ProcessLifecycleObserver() }

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)
    }
}
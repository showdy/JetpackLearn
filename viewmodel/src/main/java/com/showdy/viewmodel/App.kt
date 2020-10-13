package com.showdy.viewmodel

import android.app.Application
import android.content.Context

/**
 * Created by <b>Showdy</b> on 2020/10/13 15:40
 *
 */
class App:Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        xcrash.XCrash.init(this)
    }
}
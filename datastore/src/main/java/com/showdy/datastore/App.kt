package com.showdy.datastore

import android.app.Application
import kotlin.properties.Delegates

/**
 * Created by **Showdy** on 2020/10/9 19:21
 */
class App:Application(){

    companion object{
        var instance:App by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}
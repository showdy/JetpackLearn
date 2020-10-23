package com.showdy.alarm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.showdy.alarm.util.ActivityManager

/**
 * Created by <b>Showdy</b> on 2020/10/23 18:05
 *
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.getInstance().addActivity(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }
}
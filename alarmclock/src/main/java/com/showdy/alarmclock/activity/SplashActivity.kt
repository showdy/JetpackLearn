package com.showdy.alarmclock.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.showdy.alarmclock.R
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * Created by <b>Showdy</b> on 2020/10/16 10:35
 *
 * 欢迎页做一些权限申请处理
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView((R.layout.activity_splash))
        //简单处理，不处理再次询问申请的操作

        XXPermissions.with(this)
            .permission(
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.BODY_SENSORS,
                Permission.ACTIVITY_RECOGNITION,
                Permission.SYSTEM_ALERT_WINDOW,
            )
            .request(object : OnPermission {
                override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                    Log.d(TAG, "hasPermission: ")
                    if (all) {
                        Intent(this@SplashActivity, MainActivity::class.java).apply {
                            startActivity(
                                this
                            )
                        }
                        finish()
                    }
                }

                override fun noPermission(denied: MutableList<String>?, never: Boolean) {
                    finish()
                }
            })
    }

}
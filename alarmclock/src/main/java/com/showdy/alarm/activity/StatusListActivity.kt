package com.showdy.alarm.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.showdy.alarm.R
import com.showdy.alarm.widget.StatusClockView
import kotlinx.android.synthetic.main.activity_status_list.*

/**
 * Created by <b>Showdy</b> on 2020/10/19 16:59
 *
 */
class StatusListActivity : BaseActivity() {


    companion object {
        const val REQUEST_CODE = 1001

        fun start(context: Context) {
            Intent(context, StatusListActivity::class.java).apply {
                if (context !is Activity) flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(this)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_list)


        clockView.setClockListener(object :StatusClockView.ClockListener{
            override fun onTopClicked(text:String) {
                StatusClockActivity.startActivity(this@StatusListActivity,text)
            }

            override fun onLeftClicked(text:String) {
                StatusClockActivity.startActivity(this@StatusListActivity,text)
            }

            override fun onRightClicked(text:String) {
                StatusClockActivity.startActivity(this@StatusListActivity,text)
            }
        })
    }
}
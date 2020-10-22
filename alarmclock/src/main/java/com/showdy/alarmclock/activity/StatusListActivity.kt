package com.showdy.alarmclock.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.showdy.alarmclock.R
import kotlinx.android.synthetic.main.activity_status_list.*

/**
 * Created by <b>Showdy</b> on 2020/10/19 16:59
 *
 */
class StatusListActivity : AppCompatActivity(), View.OnClickListener {


    companion object {
        const val STATUS = "status"

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

        normal.setOnClickListener(this)
        odd.setOnClickListener(this)
        slow.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Intent(this, StatusClockActivity::class.java)
            .apply {
                putExtra(STATUS, (v as TextView).text)
            }.run { startActivity(this) }
    }
}
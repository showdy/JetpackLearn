package com.showdy.alarm.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.showdy.alarm.R
import com.showdy.alarm.data.Clock
import com.showdy.alarm.WatchViewModel
import com.showdy.alarm.util.ActivityManager
import com.showdy.alarm.util.CLOCK_IN
import com.showdy.alarm.util.createFile
import com.showdy.alarm.util.format
import kotlinx.android.synthetic.main.activity_status_clock.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/19 17:12
 *
 *  前三十分钟身体状态打卡
 */
class StatusClockActivity : BaseActivity() {

    private val clockFile by lazy { CLOCK_IN.createFile() }

    private val viewModel by viewModels<WatchViewModel>()

    companion object {

        const val STATUS = "status"

        fun startActivity(context: Context, status: String) {
            val intent = Intent(context, StatusClockActivity::class.java)
            intent.putExtra(STATUS, status)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_clock)
        //设置上一次身体状态
        val lastStatus = intent.getStringExtra(STATUS)
        status.text = if (lastStatus.isNullOrEmpty()) "未记录状态" else lastStatus
        cancel.setOnClickListener { finish() }
        ok.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val clock = Clock(Clock.STATUS_CLOCK, Date().format(), lastStatus!!)
                clockFile.appendText(clock.toString())
                viewModel.insertClock(clock)
            }
            //记录到本地
            viewModel.saveKey(WatchViewModel.KEY_LAST_STATUS, lastStatus!!)
            ActivityManager.getInstance().finishActivity(StatusListActivity::class.java)
            finish()
        }
    }

}
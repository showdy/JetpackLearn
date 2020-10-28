package com.showdy.alarm.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.showdy.alarm.R
import com.showdy.alarm.data.Clock
import com.showdy.alarm.WatchViewModel
import com.showdy.alarm.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/16 17:44
 *
 *  服药打卡
 */
class MedClockActivity : BaseActivity() {

    private val clockFile by lazy { CLOCK_IN.createFile() }

    private val viewModel by viewModels<WatchViewModel>()


    companion object {

        const val REQUEST_CODE = 100

        const val KEY_LAST_MED = "last_med"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_in)

        initTimePicker()


    }

    private fun initTimePicker() {
        TimePickerBuilder(this) { date, _ ->
            selectTimeClock(date)
        }
            .setDate(Calendar.getInstance())
            .setTimeSelectChangeListener { Log.i("clock", "onTimeSelectChanged") }
            .setType(booleanArrayOf(false, false, false, true, true, false))
            .addOnCancelClickListener { finish() }
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setDecorView(findViewById(R.id.timepicker))
            .build()
            .show()
    }

    /**
     * 选择时间打卡
     */
    private fun selectTimeClock(date: Date) {
        //写入文件
        lifecycleScope.launch(Dispatchers.IO) {
            val clock = Clock(Clock.MED_CLOCK, Date().format(), "")
            clockFile.appendText(clock.toString())
            viewModel.insertClock(clock)
        }
        //记录本次打卡信息
        val time = date.format()
        viewModel.saveKey(WatchViewModel.KEY_LAST_MED, time)
        intent.apply { putExtra(KEY_LAST_MED, time) }.run {
            setResult(Activity.RESULT_OK, this)
        }
        finish()
        Toast.makeText(this@MedClockActivity, "打卡成功", Toast.LENGTH_SHORT).show()
    }
}
package com.showdy.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class LiveDataActivity : AppCompatActivity() {

    private val livedataVm: LiveDataViewModel by viewModels { LiveDataVMFactory }

    //第二种实现，java方式
    private lateinit var viewModel: LiveDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //非属性代理方式实现
        viewModel = ViewModelProvider(this, LiveDataVMFactory).get(LiveDataViewModel::class.java)

        //观察当前时间戳
        viewModel.currentTime.observe(this) {
            time.text = it.toString()
        }

        //观察当前时间
        viewModel.currentTimeTransformed.observe(this) {
            time_transformed.text = it
        }

        //观察缓存值
        viewModel.cacheValue.observe(this) {
            cached_value.text  = it
        }

        //获取新的天气信息
        viewModel.currentWeather.observe(this) {
            current_weather.text = it
        }

        refresh_button.setOnClickListener {
            viewModel.onRefresh()
        }

    }
}

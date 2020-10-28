package com.showdy.alarm.service.observer

import android.content.Context
import android.hardware.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlin.collections.HashMap

/**
 * Created by <b>Showdy</b> on 2020/10/26 13:44
 *
 *  使用Logger日志器去写文件,GC过大，服务容易挂掉
 */
@Deprecated(message = "deprecated")
class LoggerSensorObserver(val context: Context) :
    DefaultLifecycleObserver, SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensorMap: MutableMap<Int, Sensor> = HashMap()

    @ObsoleteCoroutinesApi
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        sensorManager.getSensorList(Sensor.TYPE_ALL).forEach {
            if (it.type == Sensor.TYPE_ACCELEROMETER
                || it.type == Sensor.TYPE_HEART_BEAT
                || it.type == Sensor.TYPE_LIGHT
                || it.type == Sensor.TYPE_MAGNETIC_FIELD
                || it.type == Sensor.TYPE_STEP_COUNTER
                || it.type == Sensor.TYPE_GYROSCOPE
            ) {
                sensorMap[it.type] = it
            }
        }
    }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        //注册
        sensorMap.forEach {
            sensorManager.registerListener(this, it.value, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }


    override fun onDestroy(owner: LifecycleOwner) {
        sensorManager.unregisterListener(this)
        super.onDestroy(owner)

    }

    private var lastEvent: SensorEvent? = null


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { e ->
            if (!checkValid(e.values)) return
            var tag = ""
            lastEvent?.let {
                if (e.timestamp - it.timestamp > 1 * 1000 * 1000 * 1000) {
                    tag = "Interval"
                }
            }
            if (tag.isNotEmpty()) {
                //写入到文件
                Logger.t(e.sensor.name).d("${e.values.joinToString(",")},${e.timestamp},${tag}")
            } else {
                //写入到文件
                Logger.t(e.sensor.name).d("${e.values.joinToString(",")},${e.timestamp}")
            }
            lastEvent = event
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {


    }

    private fun checkValid(values: FloatArray): Boolean {
        values.forEach {
            if (it == 0.0f) return false
        }
        return true
    }
}
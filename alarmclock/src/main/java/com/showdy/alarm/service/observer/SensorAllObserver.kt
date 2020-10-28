package com.showdy.alarm.service.observer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarm.util.SizedSensorFile
import com.showdy.alarm.util.millFormat
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by <b>Showdy</b> on 2020/10/26 13:44
 *
 *  传感器获取数据并保存到文件
 */
class SensorAllObserver(val context: Context, private val scope: LifecycleCoroutineScope) :
    DefaultLifecycleObserver, SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


    private val sensorMap: MutableMap<Int, Sensor> = HashMap(6)
    private val sensorChannels: MutableMap<Int, SendChannel<String>> = HashMap(6)
    private val valueList: MutableMap<Int, ArrayList<String>> = HashMap(6)

    //用于统计的map
    private val lastEventMap: MutableMap<Int, Long> = HashMap(6)
    private val frequencyMap: MutableMap<Int, Long> = HashMap(6)
    private val freCounterMap: MutableMap<Int, Int> = HashMap(6)


    @ObsoleteCoroutinesApi
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        sensorManager.getSensorList(Sensor.TYPE_ALL).forEach {
            if (it.type == Sensor.TYPE_ACCELEROMETER
//                || it.type == Sensor.TYPE_HEART_BEAT
                || it.type == Sensor.TYPE_LIGHT
                || it.type == Sensor.TYPE_MAGNETIC_FIELD
                || it.type == Sensor.TYPE_STEP_COUNTER
                || it.type == Sensor.TYPE_GYROSCOPE
            ) {
                sensorMap[it.type] = it
                sensorChannels[it.type] = createActor(it.name)
                valueList[it.type] = createList(it.type)
                lastEventMap[it.type] = 0
                frequencyMap[it.type] = 0
                freCounterMap[it.type] = 0

            }
        }

    }

    private fun createList(type: Int): ArrayList<String> {
        return when (type) {
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_STEP_COUNTER -> ArrayList(getCapacity(type))
            else -> ArrayList(getCapacity(type))
        }
    }

    private fun getCapacity(type: Int): Int {
        return when (type) {
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_STEP_COUNTER -> 50
            else -> 5000
        }
    }


    @ObsoleteCoroutinesApi
    private fun createActor(name: String): SendChannel<String> {
        return scope.actor(capacity = Channel.UNLIMITED) {
            this.receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collect {
                    //由于不能使用动态创建属性代理，故而每次检查
                    val file = SizedSensorFile(context, name).getValue()
                    file.appendText("$it,\n")
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
        valueList.forEach {
            sensorChannels[it.key]?.offer(it.value.joinToString(",\n"))
        }
        sensorChannels.forEach {
            it.value.close()
        }
        super.onDestroy(owner)

    }

    private val NS2S = 1.0f / 1000000000.0f

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { e ->
            if (!checkValid(e.values)) return
            val sensor = sensorMap[e.sensor.type]
            //不能重复调用，每次调用值都不同
            val timeStamp = e.timestamp
            sensor?.let { s ->
                valueList[s.type]?.let { l ->
                    //--------统计两个数据之间是否相差超过一秒----
                    val tag = calculateOddEvent(s, timeStamp)
                    //----------------------------------------------
                    l.add("${Date().millFormat()},${e.values.joinToString(",")},${timeStamp},${tag}")
                    if (l.size == getCapacity(s.type)) {
                        sensorChannels[s.type]?.offer(l.joinToString(",\n"))
                        l.clear()
                    }
                    //------------统计频率-------
                    calculateFrequency(s, timeStamp)
                }
            }
        }
    }

    /**
     * 统计两个同源数据之间的时间差值
     */
    private fun calculateOddEvent(s: Sensor, timestamp: Long): String {
        val sb = StringBuilder("")
        val time = lastEventMap[s.type]!!
        if (time != 0L && (timestamp - time) * NS2S >= 1) {
            sb.append("Noted")
        }
        //赋值
        lastEventMap[s.type] = timestamp
        return sb.toString()
    }

    /**
     * 统计同源数据采集的频率
     */
    private fun calculateFrequency(s: Sensor, timestamp: Long) {
        val time = frequencyMap[s.type]!!
        val counter = freCounterMap[s.type]!!
        if (time != 0L) {
            if ((timestamp - time) * NS2S >= 1) {
                freCounterMap[s.type] = counter + 1
                Logger.t("Counter_${s.name}").d("$counter")
                Log.d("counter_${s.name}", "$counter")
                frequencyMap[s.type] = timestamp
                freCounterMap[s.type] = 0
            } else {
                freCounterMap[s.type] = counter + 1
            }
        } else {
            freCounterMap[s.type] = counter + 1
            frequencyMap[s.type] = timestamp
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
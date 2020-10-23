package com.showdy.alarm.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarm.util.SensorFile
import com.showdy.alarm.util.millFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by <b>Showdy</b> on 2020/10/12 19:25
 **
 * 传感器获取数据并保存到文件
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class SensorObserver(
    private val context: Context,
    private val scope: LifecycleCoroutineScope,
) : DefaultLifecycleObserver, SensorEventListener {

    companion object {
        const val TAG = "SensorObserver"
        const val FAST_LIST_SIZE = 5000
        const val SLOW_LIST_SIZE = 50
        const val SAMPLE_PERIOD = 10000
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var acceleratorSensor: Sensor //加速度
    private lateinit var lightSensor: Sensor  //重力
    private lateinit var heartRateSensor: Sensor //心率
    private lateinit var gyroscopeSensor: Sensor //陀螺仪
    private lateinit var stepSensor: Sensor //计步
    private lateinit var magneticSensor: Sensor //地磁

    private lateinit var lightActor: SendChannel<String>
    private lateinit var stepActor: SendChannel<String>
    private lateinit var gyroscopeActor: SendChannel<String>
    private lateinit var magneticActor: SendChannel<String>
    private lateinit var heartRateActor: SendChannel<String>
    private lateinit var acceleratorActor: SendChannel<String>

    private val lightList = ArrayList<String>(FAST_LIST_SIZE)
    private val acceList = ArrayList<String>(FAST_LIST_SIZE)
    private val gyoscopeList = ArrayList<String>(FAST_LIST_SIZE)
    private val magneticList = ArrayList<String>(FAST_LIST_SIZE)
    private val heartRateList = ArrayList<String>(FAST_LIST_SIZE)
    private val stepList = ArrayList<String>(FAST_LIST_SIZE)

    private val acceleratorFile: File by SensorFile(context, "accelerator")
    private val lightFile: File by SensorFile(context, "light")
    private val magneticFiledFile: File by SensorFile(context, "magnetic")
    private val stepFile: File by SensorFile(context, "step")
    private val gyroscopeFile: File by SensorFile(context, "gyroscope")
    private val heartRateFile: File by SensorFile(context, "heart")


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        //开始传感器数据采集
        sensorManager.registerListener(this, acceleratorSensor, SAMPLE_PERIOD)
        sensorManager.registerListener(this, lightSensor, SAMPLE_PERIOD)
        sensorManager.registerListener(this, heartRateSensor, SAMPLE_PERIOD)
        sensorManager.registerListener(this, gyroscopeSensor, SAMPLE_PERIOD)
        sensorManager.registerListener(this, stepSensor, SAMPLE_PERIOD)
        sensorManager.registerListener(this, magneticSensor, SAMPLE_PERIOD)
        //开启一个读数据并写入文件的线程
        startReadSensorResultCoroutines()
    }

    private fun startReadSensorResultCoroutines() {
        lightActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    lightFile.appendText(it)
                }
        }

        stepActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    stepFile.appendText(it)
                }
        }

        magneticActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    magneticFiledFile.appendText(it)
                }
        }

        acceleratorActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    acceleratorFile.appendText(it)
                }

        }

        heartRateActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    heartRateFile.appendText(it)
                }
        }

        gyroscopeActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    gyroscopeFile.appendText(it)
                }
        }
    }



    override fun onDestroy(owner: LifecycleOwner) {
        //停止采集
        sensorManager.unregisterListener(this)
        //关闭协程
        lightActor.close()
        stepActor.close()
        gyroscopeActor.close()
        heartRateActor.close()
        magneticActor.close()
        acceleratorActor.close()
        super.onDestroy(owner)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (!checkValid(it.values)) {
                return
            }
            when (event.sensor.type) {
                Sensor.TYPE_LIGHT -> {
                    sendSensorResult(it, lightList, lightActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    sendSensorResult(it, magneticList, magneticActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    sendSensorResult(it, stepList, stepActor, SLOW_LIST_SIZE)
                }

                Sensor.TYPE_HEART_RATE -> {
                    sendSensorResult(it, heartRateList, heartRateActor, SLOW_LIST_SIZE)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    sendSensorResult(it, gyoscopeList, gyroscopeActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    sendSensorResult(it, acceList, acceleratorActor, FAST_LIST_SIZE)
                }
                else -> {
                }
            }
        }
    }

    private fun sendSensorResult(
        event: SensorEvent,
        list: ArrayList<String>,
        actor: SendChannel<String>,
        maxSize: Int
    ) {
        list.add("${Date().millFormat()},${event.values.joinToString(",")}")
        if (list.size == maxSize) {
            actor.offer(list.joinToString(",\n"))
            list.clear()
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
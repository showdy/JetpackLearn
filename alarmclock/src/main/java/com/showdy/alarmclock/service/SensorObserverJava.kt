package com.showdy.alarmclock.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File


/**
 * Created by <b>Showdy</b> on 2020/10/12 19:25
 **
 * 传感器获取数据并保存到文件
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class SensorObserverJava(
    private val context: Context,
    private val scope: LifecycleCoroutineScope,
) : DefaultLifecycleObserver, SensorEventListener {

    companion object {
        const val TAG = "SensorObserver"
        const val FAST_LIST_SIZE = 5000
        const val SLOW_LIST_SIZE = 50
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var acceleratorSensor: Sensor //加速度
    private lateinit var gravitySensor: Sensor  //重力
    private lateinit var heartRateSensor: Sensor //心率
    private lateinit var gyroscopeSensor: Sensor //陀螺仪
    private lateinit var stepSensor: Sensor //计步
    private lateinit var magneticSensor: Sensor //地磁

    private lateinit var acceleratorFile: File
    private lateinit var gravityFile: File
    private lateinit var magneticFiledFile: File
    private lateinit var stepFile: File
    private lateinit var gyroscopeFile: File
    private lateinit var heartRateFile: File

    private lateinit var gravityActor: SendChannel<String>
    private lateinit var stepActor: SendChannel<String>
    private lateinit var gyroscopeActor: SendChannel<String>
    private lateinit var magneticActor: SendChannel<String>
    private lateinit var heartRateActor: SendChannel<String>
    private lateinit var acceleratorActor: SendChannel<String>

    private val gravityList = ArrayList<String>(FAST_LIST_SIZE)
    private val acceList = ArrayList<String>(FAST_LIST_SIZE)
    private val gyoscopeList = ArrayList<String>(FAST_LIST_SIZE)
    private val magneticList = ArrayList<String>(FAST_LIST_SIZE)
    private val heartRateList = ArrayList<String>(FAST_LIST_SIZE)
    private val stepList = ArrayList<String>(FAST_LIST_SIZE)


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        acceleratorFile = createFile("accelerator")
        gravityFile = createFile("gravity")
        magneticFiledFile = createFile("magnetic")
        stepFile = createFile("step")
        gyroscopeFile = createFile("gyroscope")
        heartRateFile = createFile("heart_rate")

    }

    private fun createFile(fileName: String): File {
        val file =
            File(context.applicationContext.filesDir.path + "/${fileName}.txt")
        if (!file.exists()) file.createNewFile()
        file.setWritable(true)
        return file
    }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        //开始传感器数据采集
        sensorManager.registerListener(this, acceleratorSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST)
        //开启一个读数据并写入文件的线程
        startReadSensorResultCoroutines()
    }

    private fun startReadSensorResultCoroutines() {
        gravityActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    gravityFile.appendText(it)
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

    private fun writeFile(
        result: String,
        list: ArrayList<String>,
        file: File,
        maxSize: Int = FAST_LIST_SIZE
    ) {
        list.add(result)
        if (list.size == maxSize) {
            file.appendText(list.toString())
            list.clear()
        }
    }


    override fun onDestroy(owner: LifecycleOwner) {
        //停止采集
        sensorManager.unregisterListener(this)
        //关闭协程
        gravityActor.close()
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
                Sensor.TYPE_GRAVITY -> {
                    sendSensorResult(it,gravityList,gravityActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    sendSensorResult(it,magneticList,magneticActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    sendSensorResult(it,stepList,stepActor, SLOW_LIST_SIZE)
                }

                Sensor.TYPE_HEART_RATE -> {
                    sendSensorResult(it,heartRateList,heartRateActor, SLOW_LIST_SIZE)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    sendSensorResult(it,gyoscopeList,gyroscopeActor, FAST_LIST_SIZE)
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    sendSensorResult(it,acceList,acceleratorActor, FAST_LIST_SIZE)
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
    ){
        list.add("${event.timestamp}=${event.values.joinToString(",")}")
        if (list.size== maxSize){
            actor.offer(list.toString())
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
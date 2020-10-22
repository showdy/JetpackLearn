package com.showdy.alarmclock.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import com.showdy.alarmclock.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File


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

    private lateinit var gravityActor: SendChannel<SResult>
    private lateinit var stepActor: SendChannel<SResult>
    private lateinit var gyroscopeActor: SendChannel<SResult>
    private lateinit var magneticActor: SendChannel<SResult>
    private lateinit var heartRateActor: SendChannel<SResult>
    private lateinit var acceleratorActor: SendChannel<SResult>

    private lateinit var result: SResult

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
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    gravityFile.appendText("$it\r\n")
                }
        }

        stepActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    stepFile.appendText("$it\n")
                }
        }

        magneticActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    magneticFiledFile.appendText("$it\n")
                }
        }

        acceleratorActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    acceleratorFile.appendText("$it\n")
                }
        }

        heartRateActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    heartRateFile.appendText("$it\n")
                }
        }

        gyroscopeActor = scope.actor(capacity = Channel.UNLIMITED) {
            receiveAsFlow()
                .buffer()
                .filter { checkValid(it.values) }
                .collectLatest {
                    Log.d(TAG, "received: $it")
                    gyroscopeFile.appendText("$it\n")
                }
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

            //todo:此处协程开启太耗性能，有何解决办法？
            scope.launch {
                result = SResult(event.timestamp, event.values)
                when (event.sensor.type) {
                    Sensor.TYPE_GRAVITY -> {
                        Log.d(TAG, "gravity: ${event.values.joinToString(",")}")
                        gravityActor.send(result)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        Log.d(TAG, "magnetic: ${event.values.joinToString(",")}")
                        magneticActor.send(result)
                    }
                    Sensor.TYPE_STEP_COUNTER -> {
                        Log.d(TAG, "step = ${event.values.joinToString(",")}")
                        stepActor.send(result)
                    }

                    Sensor.TYPE_HEART_RATE -> {
                        Log.d(TAG, "heartRate = ${event.values.joinToString(",")}")
                        heartRateActor.send(result)
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        Log.d(TAG, "gyroscope = ${event.values.joinToString(",")}")
                        gyroscopeActor.send(result)
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        Log.d(TAG, "accelerator = ${event.values.joinToString(",")}")
                        acceleratorActor.send(result)
                    }
                }
            }

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
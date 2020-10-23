package com.showdy.alarm.util

import android.content.Context
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by <b>Showdy</b> on 2020/10/20 17:07
 *
 * 每半小时间创建一个新文件名
 */
class SensorFile(context: Context, private val fileName: String) : ReadOnlyProperty<Any?, File> {

    private val dirFile: File = File(context.applicationContext.filesDir, fileName)
    private var createdTime = System.currentTimeMillis()

    init {
        if (!dirFile.exists()) dirFile.mkdirs()
        dirFile.setWritable(true)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): File {
        val currentTime = System.currentTimeMillis()
        val file: File = if (currentTime - createdTime < TIME_INTERVAL) {
            File(dirFile, "${fileName}-${createdTime}.csv")
        } else {
            createdTime = currentTime
            File(dirFile, "${fileName}-${currentTime}.csv")
        }
        if (!file.exists()) file.createNewFile()
        file.setWritable(true)
        return file
    }

    companion object {
        const val TIME_INTERVAL = 30 * 60 * 1000
    }
}
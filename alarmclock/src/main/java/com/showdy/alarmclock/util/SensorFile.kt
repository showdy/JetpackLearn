package com.showdy.alarmclock.util

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

    var fileDir: File = context.filesDir

    var createdTime = System.currentTimeMillis()


    override fun getValue(thisRef: Any?, property: KProperty<*>): File {
        val currentTime = System.currentTimeMillis()
        val file: File = if (currentTime - createdTime < 30 * 60 * 1000) {
            File(fileDir, "/${fileName}-${createdTime}.txt")
        } else {
            createdTime = currentTime
            File(fileDir, "/${fileName}-${currentTime}.txt")
        }
        if (file.exists()) file.createNewFile()
        file.setWritable(true)
        return file
    }
}
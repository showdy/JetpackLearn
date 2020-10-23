package com.showdy.alarm.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:38
 *
 */
@Entity(tableName = "gyoscope")
data class Gyoscope(
    @ColumnInfo(name = "值")
    val values: String,
    @ColumnInfo(name = "时间戳")
    val timeStamp: Long = System.currentTimeMillis()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
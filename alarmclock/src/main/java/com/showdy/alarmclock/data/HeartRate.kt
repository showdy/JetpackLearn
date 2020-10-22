package com.showdy.alarmclock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:38
 *
 */
@Entity(tableName = "heartRate")
data class HeartRate(
    @ColumnInfo(name="值")
    val values: String,
    @ColumnInfo(name="时间戳")
    val timeStamp: Long
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
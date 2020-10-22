package com.showdy.alarmclock.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarmclock.data.HeartRate

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface HeartRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(heartRate: HeartRate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(heartRate:List<HeartRate>)
}
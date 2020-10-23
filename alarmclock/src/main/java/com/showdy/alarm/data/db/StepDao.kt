package com.showdy.alarm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarm.data.Step

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: Step)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(step:List<Step>)
}
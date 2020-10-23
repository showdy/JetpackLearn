package com.showdy.alarm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarm.data.Accelerator

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface AcceleratorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accelerator:Accelerator)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(accelerator:List<Accelerator>)
}
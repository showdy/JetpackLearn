package com.showdy.livedata

import androidx.lifecycle.LiveData

/**
 * Created by <b>Showdy</b> on 2020/10/12 16:31
 *
 */
interface IDataStore {
    fun getCurrentTime(): LiveData<Long>
    fun fetchWeather(): LiveData<String>
    val cacheData: LiveData<String>
    suspend fun fetchNewData()
}
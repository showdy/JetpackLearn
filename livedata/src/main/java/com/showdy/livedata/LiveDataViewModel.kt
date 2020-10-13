package com.showdy.livedata

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/12 16:48
 *
 */
class LiveDataViewModel(private val dataSource: IDataStore):ViewModel() {

    companion object{
        const val LOADING_STRING = "Loading..."
    }


    val currentTime = dataSource.getCurrentTime()

    val currentTimeTransformed = currentTime.switchMap {
        liveData { emit(timeStampToTime(it)) }
    }


    val currentWeather:LiveData<String> = liveData {
        emit(LOADING_STRING)
        emitSource(dataSource.fetchWeather())
    }

    //// Exposed cached value in the data source that can be updated later on
    val cacheValue = dataSource.cacheData


    // // Called when the user clicks on the "FETCH NEW DATA" button. Updates value in data source.
    fun onRefresh(){
        viewModelScope.launch {
            //// Launch a coroutine that reads from a remote data source and updates cache
            dataSource.fetchNewData()
        }
    }


    //// Simulates a long-running computation in a background thread
    private suspend fun timeStampToTime(timeStamp:Long):String{
        // Simulate long operation
        delay(500)
        val date = Date(timeStamp)
        return date.toString()
    }
}
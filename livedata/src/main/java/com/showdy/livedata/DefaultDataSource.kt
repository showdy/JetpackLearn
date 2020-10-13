package com.showdy.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Created by <b>Showdy</b> on 2020/10/12 16:35
 *
 */
class DefaultDataSource(private val ioDispatcher: CoroutineDispatcher) : IDataStore {

    private val weatherConditions = listOf("sunny", "cloud", "Rainy", "Stormy")


    // Cache data that is exposed to VM
    private val _cacheData = MutableLiveData("This is old data")
    override val cacheData: LiveData<String> = _cacheData

    /**
     * LiveData generate a value that will be transformed
     */
    override fun getCurrentTime(): LiveData<Long> {
        return liveData {
            while (true) {
                emit(System.currentTimeMillis())
                delay(1000)
            }
        }
    }

    /**
     * every 2 seconds,expose a livedata of changing weather conditions
     */
    override fun fetchWeather(): LiveData<String> {
        return liveData {
            var counter = 0
            while (true) {
                counter++
                delay(2000)
                emit(weatherConditions[counter % weatherConditions.size])
            }
        }
    }

    /**
     * Called when the cache needs to be refreshed.Must be called from coroutines
     */
    override suspend fun fetchNewData() {
        withContext(Dispatchers.Main) {
            _cacheData.value = "Fetching new data"
            _cacheData.value = simulateNetworkDataFetch()
        }
    }

    //simulate data from network
    private var counter = 0
    private suspend fun simulateNetworkDataFetch(): String {
      return  withContext(ioDispatcher) {
            delay(3000)
            "New data from request ${++counter}"
        }
    }
}
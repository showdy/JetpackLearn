package com.showdy.lifecycle

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Created by <b>Showdy</b> on 2020/10/12 17:49
 *
 *  bug原因不明....
 */
class LocationObserver private constructor() : DefaultLifecycleObserver {

    companion object {
        @JvmStatic
        operator fun invoke(): LocationObserver = LocationObserver()
    }

    private val _startLocation: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _location = MutableLiveData(Location("Loading..."))

    val location: LiveData<Location> = _location


    //获取到的定位信息
    //用法有问题？？？--不会自动更新
//    val locations: LiveData<Location> = _startLocation.switchMap {
//        Log.d("StartLocation", "$it")
//        liveData {
//            if (it) {
//                emit(fetchLocation())
//            } else {
//                emit(Location("Null"))
//            }
//        }
//
//    }

    //模拟耗时的定位信息
    private suspend fun fetchLocation(): Location {
        return withContext(Dispatchers.IO) {
            Location("Loading...")
            delay(2000)
            Location("SZ-${System.currentTimeMillis()}")
        }
    }


    //start location
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        //模拟定位信息，每2s更新一次位置信息
//        _startLocation.value = true

        _location.postValue(Location("SZ-${System.currentTimeMillis()}"))
    }


    //stop locate
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
//        _startLocation.value = false
        _location.value = null
    }
}
package com.showdy.livedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalArgumentException

/**
 * Created by <b>Showdy</b> on 2020/10/12 16:55
 *
 *  Factory for VM, 主要用来构建带有参数的VM
 */
object LiveDataVMFactory : ViewModelProvider.Factory {

    private val dataSource = DefaultDataSource(Dispatchers.IO)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveDataViewModel::class.java)) {
            return LiveDataViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.showdy.alarm

import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.showdy.alarm.data.Clock
import com.showdy.alarm.data.db.WatchDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by <b>Showdy</b> on 2020/10/19 11:05
 *
 * 全局ViewModel
 */
class WatchViewModel : ViewModel() {

    companion object {
        const val PREFERENCE_NAME = "watch_data"

        const val KEY_LAST_MED = "last_clock"

        const val KEY_LAST_STATUS = "last_status"
    }

    var dataStore: DataStore<Preferences> = App.instance.createDataStore(PREFERENCE_NAME)

    val watchDb = WatchDb(App.instance)


    private suspend fun save(key: Preferences.Key<String>, value: String) {
        dataStore.edit {
            it[key] = value
        }
    }

    private fun read(key: Preferences.Key<String>): Flow<String> {
        return dataStore.data
            .catch {
                when (it) {
                    is IOException -> emit(emptyPreferences())
                    else -> throw  it
                }
            }
            .map {
                it[key] ?: ""
            }
    }

    fun saveKey(key: String, value: String) {
        viewModelScope.launch {
            save(preferencesKey(key), value)
        }
    }

    fun readKey(key: String): LiveData<String> {
        return read(preferencesKey(key)).asLiveData()
    }

    fun insertClock(clock:Clock){
        watchDb.clockDao().insert(clock)
    }
}
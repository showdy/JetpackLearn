package com.showdy.datastore

import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.*
import com.showdy.datastore.data.DataStoreRepository
import com.showdy.datastore.data.IDataStoreRepository
import com.showdy.datastore.data.ISpRepository
import com.showdy.datastore.data.SharedPreferencesRepository
import kotlinx.coroutines.launch

/**
 * Created by <b>Showdy</b> on 2020/10/9 18:13
 *
 */
class StoreViewModel : ViewModel() {

    private val spRepository: ISpRepository = SharedPreferencesRepository(context = App.instance)
    private val dataStoreRepository: IDataStoreRepository =
        DataStoreRepository(context = App.instance)


    fun saveBySharedPreferences(key: String) = spRepository.save(key)

    fun readBySharedPreferences(key: String) = spRepository.read(key)


    fun saveByDataStore(key: Preferences.Key<Boolean>) {
        viewModelScope.launch {
            dataStoreRepository.save(key)
        }
    }

    fun readByDataStore(key: Preferences.Key<Boolean>): LiveData<Boolean> {
        return dataStoreRepository.read(key).asLiveData()
    }

    fun migrationSharedPreferences() = dataStoreRepository.migrationSharedPreferencesToDataStore()

    fun testMigration(key: Preferences.Key<Boolean>) = dataStoreRepository.read(key)


    object PreferencesKeys {
        // SharedPreferences 的测试的 key
        const val KEY_SP_QQ = "QQ"

        // DataStore 的测试的 key
        val KEY_QQ = preferencesKey<Boolean>("QQ")
        val KEY_WECHAT = preferencesKey<Boolean>("weChat")
        val KEY_GITHUB = preferencesKey<Boolean>("github")
    }

}
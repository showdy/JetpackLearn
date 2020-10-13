package com.showdy.datastore.data

import androidx.datastore.preferences.Preferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by <b>Showdy</b> on 2020/10/9 18:01
 *
 */

interface IDataStoreRepository{

    suspend fun save(key: Preferences.Key<Boolean>)

    fun read(key: Preferences.Key<Boolean>): Flow<Boolean>

    fun migrationSharedPreferencesToDataStore()
}
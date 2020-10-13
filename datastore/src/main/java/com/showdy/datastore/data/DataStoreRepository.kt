package com.showdy.datastore.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Created by <b>Showdy</b> on 2020/10/9 17:14
 *
 * DataStore存储键值对
 */
class DataStoreRepository(private val context: Context) : IDataStoreRepository {

    companion object {
        const val PREFERENCE_NAME = "DataStore"
    }

    var dataStore: DataStore<Preferences> = context.createDataStore(PREFERENCE_NAME)


    override suspend fun save(key: Preferences.Key<Boolean>) {
        dataStore.edit {
            val value = it[key] ?: false
            it[key] = !value
        }
    }

    override fun read(key: Preferences.Key<Boolean>): Flow<Boolean> {
       return dataStore.data
            .catch {
                when (it) {
                    is IOException -> {
                        it.printStackTrace()
                        emit(emptyPreferences())
                    }
                    else -> throw  it
                }
            }
            .map {
                it[key] ?: false
            }

    }

    //SharedPreferences迁移到DataStore
    override fun migrationSharedPreferencesToDataStore() {
        dataStore = context.createDataStore(
            name = PREFERENCE_NAME,
            migrations = listOf(
                SharedPreferencesMigration(context,context.packageName + "_preferences")
            )
        )
    }
}
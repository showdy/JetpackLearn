//package com.gyenno.watch.util
//
//import android.content.Context
//import android.content.pm.PackageManager
//import androidx.datastore.DataStore
//import androidx.datastore.preferences.*
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.asLiveData
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//import java.io.IOException
//import kotlin.properties.ReadWriteProperty
//import kotlin.reflect.KProperty
//
///**
// * Created by <b>Showdy</b> on 2020/10/19 21:39
// *
// */
//class DataStoreDelegate<T>(
//    val context: Context,
//    val name: String? = null,
//    private val default: T,
//    private val fileName: String = context.packageName + "/data_store",
//    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
//) : ReadWriteProperty<Any?, T> {
//
//
//    private val dataStore: DataStore<Preferences> by lazy {
//        context.createDataStore(fileName)
//    }
//
//
//    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
//        return findPreference(findPropertyName(property)).first()
//    }
//
//
//    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
//        coroutineScope.launch {
//            putPreference(findPropertyName(property), value)
//        }
//    }
//
//    private fun findPreference(key: String): Flow<T> {
//        return dataStore.data.catch {
//            when (it) {
//                is IOException -> emit(emptyPreferences())
//                else -> throw  it
//            }
//        }.map {
//            it[preferencesKey(key)] as? T ?: default
//        }
//    }
//
//    private suspend fun putPreference(key: String, value: T) {
//        dataStore.edit {
//            it[preferencesKey(key)] = value as Any
//        }
//    }
//
//    private fun findPropertyName(property: KProperty<*>) =
//        if (name.isNullOrEmpty()) property.name else name
//
//
//}
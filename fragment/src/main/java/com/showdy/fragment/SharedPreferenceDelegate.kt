package com.showdy.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by <b>Showdy</b> on 2020/10/14 14:12
 *
 *  kotlin属性代理实现SharedPreference的存储
 */
class SharedPreferenceDelegate<T>(
    val context: Context,
    val name: String? = null,
    val default: T,
    val prefName: String? = null
) : ReadWriteProperty<Any?, T> {

    private val prefs: SharedPreferences by lazy {
        if (prefName.isNullOrBlank()) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        }
    }

    private fun findPropertyName(property: KProperty<*>) =
        if (name.isNullOrEmpty()) property.name else name


    private fun findPreference(key: String): T {
        return when (default) {
            is Long -> prefs.getLong(key, default)
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is String -> prefs.getString(key, default)
            else -> throw IllegalArgumentException("unsupported type")
        } as T
    }


    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key = findPropertyName(property))
    }


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findPropertyName(property),value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putPreference(key: String, value: T) {
        with(prefs.edit()){
            when(value){
                is Long -> putLong(key,value)
                is Int -> putInt(key,value)
                is Boolean -> putBoolean(key,value)
                is String -> putString(key,value)
                else -> throw IllegalArgumentException("unsupported type")
            }
        }.apply()
    }
}




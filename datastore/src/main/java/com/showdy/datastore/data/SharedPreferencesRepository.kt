package com.showdy.datastore.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * Created by <b>Showdy</b> on 2020/10/9 17:14
 *
 *  采用SharedPreferences存储
 */
class SharedPreferencesRepository(context: Context) : ISpRepository {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun save(key: String) {
        sharedPreferences.edit {
            putBoolean(key, !read(key))
        }
    }

    override fun read(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }
}
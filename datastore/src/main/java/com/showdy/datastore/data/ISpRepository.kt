package com.showdy.datastore.data

/**
 * Created by <b>Showdy</b> on 2020/10/9 17:13
 *
 */
interface ISpRepository {

    fun save(key: String)

    fun read(key: String):Boolean
}
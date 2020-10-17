package com.showdy.fragment

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by <b>Showdy</b> on 2020/10/14 13:34
 *
 * 属性代理的一些例子
 */

class Example {

    //申明一个属性，属性值需要去掉收尾的空格
    var param: String
        get() = ""
        set(value) {
            value.trim()
        }

    //使用属性代理申明一个属性
    var paramDelegate: String by TrimDelegate()

    //相当于
    private val delegate = TrimDelegate()
    var param2: String
        get() = delegate.getValue(this, ::param2)
        set(value) {
            delegate.setValue(this, ::param2, value)
        }
}

class TrimDelegate : ReadWriteProperty<Any?, String> {


    private var trimedValue: String = ""


    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return trimedValue
    }

    /**
     * thisRef: 表示拥有该属性的对象，比如Example对象
     *
     * property：指被委托的属性的类型，如 ::param
     */
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        value.trim()
    }
}
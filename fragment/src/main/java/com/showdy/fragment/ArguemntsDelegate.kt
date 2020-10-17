package com.showdy.fragment

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable
import java.lang.IllegalStateException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by <b>Showdy</b> on 2020/10/14 13:44
 *
 * @see {@link https://proandroiddev.com/kotlin-delegates-in-android-1ab0a715762d }
 *
 * fragment传参的例子
 */
class ArguemntsFragment : Fragment() {

    private var param1: Int = 0
    private var param2: String = ""

    companion object {
        const val PARAM1 = "param1"
        const val PARAM2 = "param2"

        //传参并实例化Fragment
        fun newInstance(param1: Int, param2: String): ArguemntsFragment {
            return ArguemntsFragment().apply {
                arguments = Bundle().apply {
                    putInt(PARAM1, param1)
                    putString(PARAM2, param2)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取参数
        arguments?.let {
            param1 = it.getInt(PARAM1)
            param2 = it.getString(PARAM2, "")
        }
    }
}

class ArguemntsFragment2 : Fragment() {

    //使用属性申明方式
    private var param1: Int?
        get() = arguments?.getInt(PARAM1)
        set(value) {
            value?.let {
                arguments?.putInt(PARAM1, it)
            } ?: arguments?.remove(PARAM1)
        }
    private var param2: String?
        get() = arguments?.getString(PARAM2)
        set(value) {
            value?.let {
                arguments?.putString(PARAM2, it)
            } ?: arguments?.remove(PARAM2)
        }

    //属性代理的方式
    private var param3: Int by argument()
    private var param4: String? by argumentNullable()


    companion object {
        const val PARAM1 = "param1"
        const val PARAM2 = "param2"

        //传参并实例化Fragment
        fun newInstance(param1: Int, param2: String,param3: Int, param4: String): ArguemntsFragment2 {
            return ArguemntsFragment2().apply {
                this.param1 = param1
                this.param2 = param2

                this.param3 = param3
                this.param4 = param4
            }
        }
    }
}

//------------------属性代理实现fragment传参---------------

fun <T : Any> argument(): ReadWriteProperty<Fragment, T> = FragmentArgumentDelegate()

fun <T : Any?> argumentNullable(): ReadWriteProperty<Fragment, T?> =
    FragmentNullableArgumentDelegate()


private fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Short -> putShort(key, value)
        is Long -> putLong(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}

/**
 * 属性值为非null
 */
class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        //获取属性名
        val propertyName = property.name
        return thisRef.arguments?.get(propertyName) as? T
            ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments
            ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        args.put(key, value)
    }
}

/**
 * 属性值为null时的代理
 */
class FragmentNullableArgumentDelegate<T : Any?> :
    ReadWriteProperty<Fragment, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T? {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
    }

    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>, value: T?
    ) {
        val args = thisRef.arguments
            ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        value?.let { args.put(key, it) } ?: args.remove(key)
    }
}
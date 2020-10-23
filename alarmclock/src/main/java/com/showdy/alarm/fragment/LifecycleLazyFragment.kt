package com.showdy.alarm.fragment

import androidx.fragment.app.Fragment

/**
 * Created by <b>Showdy</b> on 2020/10/19 10:48
 *
 * 懒加载的Fragment
 */
abstract class LifecycleLazyFragment : Fragment() {

    private var isLoaded = false

    override fun onResume() {
        super.onResume()
        //增加不可见的判断是因为要解决Fragment嵌套的问题
        if (!isLoaded && !isHidden) {
            isLoaded = true
            lazyInit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

    abstract fun lazyInit()
}
package com.showdy.alarmclock.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.showdy.alarmclock.R
import com.showdy.alarmclock.WatchViewModel
import com.showdy.alarmclock.activity.MedClockActivity
import kotlinx.android.synthetic.main.fragment_manual_clcok.*

/**
 * Created by <b>Showdy</b> on 2020/10/19 10:34
 *
 * 手动打卡界面
 */
class MedClockFragment : Fragment() {

    private val viewModel by viewModels<WatchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual_clcok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.medClock).setOnClickListener {
            startActivityForResult()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //设置上一次的打卡时间
        viewModel.readKey(WatchViewModel.KEY_LAST_MED)
            .observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    lastClock.text = "还未服药"
                } else {
                    lastClock.text = it
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MedClockActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val time = data?.getStringExtra(MedClockActivity.KEY_LAST_MED)
            lastClock.text = time ?: "还未服药"
        }
    }

    /**
     * Fragment中调用startActivityForResult要注意几种情况
     *  用getActivity方法发起调用，只有父Activity的onActivityResult会调用，Fragment中的onActivityResult不会被调用
     *  直接发起startActivityForResult调用，当前的Fragment的onActivityResult，和父Activity的onActivityResult都会调用
     *  用getParentFragment发起调用，则只有父Activity和父Fragment的onActivityResult会被调用，当前的Fragment的onActivityResult不会被调用。
     */
    private fun startActivityForResult() {
        //跳转
        Intent(requireContext(), MedClockActivity::class.java).apply {
            startActivityForResult(this, MedClockActivity.REQUEST_CODE)
        }
    }
}
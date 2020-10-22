package com.showdy.alarmclock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.showdy.alarmclock.R
import com.showdy.alarmclock.WatchViewModel

/**
 * Created by <b>Showdy</b> on 2020/10/19 11:38
 *
 */
class StatusClockFragment : Fragment() {

    private val viewModel by viewModels<WatchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interval_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        viewModel.readKey(WatchViewModel.KEY_LAST_STATUS)
            .observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    view?.findViewById<TextView>(R.id.intervalStatus)?.text = "还没状态"
                } else {
                    view?.findViewById<TextView>(R.id.intervalStatus)?.text = it
                }
            }
    }
}
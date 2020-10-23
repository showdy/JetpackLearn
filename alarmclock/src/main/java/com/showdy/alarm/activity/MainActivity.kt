package com.showdy.alarm.activity


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.showdy.alarm.R
import com.showdy.alarm.fragment.StatusClockFragment
import com.showdy.alarm.fragment.MedClockFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 打卡主页界面
 */
class MainActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager.adapter = FragmentAdapter(supportFragmentManager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    view1.visibility = View.VISIBLE
                    view3.visibility = View.GONE
                } else {
                    view1.visibility = View.GONE
                    view3.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }

    class FragmentAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        companion object {
            val listFragment = listOf<Class<out Fragment>>(
                MedClockFragment::class.java,
                StatusClockFragment::class.java
            )
        }


        override fun getCount(): Int {
            return listFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return listFragment[position].newInstance()
        }
    }
}



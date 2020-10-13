package com.showdy.lifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_life_cycle.*

class LifeCycleActivity : AppCompatActivity() {


    private val serviceIntent: Intent by lazy { Intent(this, LifeCycleService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycle)

        lifecycle.addObserver(LocationObserver())

        LocationObserver().location.observe(this) {
            location.text = it.locationName
        }
    }


    override fun onResume() {
        super.onResume()

        startService(serviceIntent)
    }


    override fun onStop() {
        super.onStop()
        stopService(serviceIntent)
    }
}

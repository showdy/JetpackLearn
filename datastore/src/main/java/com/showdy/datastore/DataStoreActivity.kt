package com.showdy.datastore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.showdy.datastore.StoreViewModel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 演示如何使用DataStore
 */
class DataStoreActivity : AppCompatActivity(), View.OnClickListener {

    private val storeViewModel: StoreViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(sc_qq) {
            isChecked = storeViewModel.readBySharedPreferences(PreferencesKeys.KEY_SP_QQ)
            setOnClickListener(this@DataStoreActivity)
        }

        sc_wehcat.setOnClickListener(this)
        sc_github.setOnClickListener(this)
        migration.setOnClickListener(this)

        initStoreObserver()
    }

    private fun initStoreObserver() {
        storeViewModel.readByDataStore(PreferencesKeys.KEY_WECHAT)
            .observe(this) {
                sc_wehcat.isChecked = it
            }

        storeViewModel.readByDataStore(PreferencesKeys.KEY_GITHUB)
            .observe(this) {
                sc_github.isChecked = it
            }
    }

    override fun onClick(view: View) {
        when (view) {
            sc_qq -> storeViewModel.saveBySharedPreferences(PreferencesKeys.KEY_SP_QQ)
            sc_wehcat -> storeViewModel.saveByDataStore(PreferencesKeys.KEY_WECHAT)
            sc_github -> storeViewModel.saveByDataStore(PreferencesKeys.KEY_GITHUB)
            migration -> {
                //迁移后，需要执行一次读或写，DataStore才会合并SharedPreferences文件内容
                storeViewModel.migrationSharedPreferences()
                lifecycleScope.launch {
                    storeViewModel.testMigration(PreferencesKeys.KEY_QQ).collect()
                }
                Toast.makeText(this, "迁移成功", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
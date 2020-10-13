package com.showdy.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class VMActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_m)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container,
                    ProductListFragment(),
                    ProductListFragment::class.java.simpleName
                )
                .commit()
        }
    }

    /**
     * show the product detail fragment
     */
    fun showProductDetail(product: Product) {
        val productFragment = ProductFragment(product.id)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(ProductFragment.TAG)
            .replace(R.id.container, productFragment, null).commit()

    }
}

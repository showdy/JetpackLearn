package com.showdy.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Created by <b>Showdy</b> on 2020/10/13 11:00
 *
 */
class ProductFragment : Fragment() {

    companion object {

        const val TAG = "product"

        private const val KEY_PRODUCT_ID = "product_id"

        operator fun invoke(productId: Int): ProductFragment {
            return ProductFragment().apply {
                arguments = Bundle().also { it.putInt(KEY_PRODUCT_ID, productId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
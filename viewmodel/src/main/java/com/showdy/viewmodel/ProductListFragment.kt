package com.showdy.viewmodel

import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_list_product.*
import kotlinx.android.synthetic.main.item_product_list.view.*

/**
 * Created by <b>Showdy</b> on 2020/10/13 10:59
 *
 *  产品列表
 */
class ProductListFragment : Fragment(R.layout.fragment_list_product) {


    private val productListViewModel by viewModels<ProductListViewModel>()

    private val productAdapter by lazy { ProductListAdapter() }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refreshLayout.setColorSchemeResources(
            android.R.color.holo_green_light,
            android.R.color.holo_red_dark, android.R.color.holo_orange_dark
        )
        refreshLayout.setOnRefreshListener {
            productListViewModel.refresh()
        }

        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = productAdapter

        //加载数据
        productListViewModel.products.observe(viewLifecycleOwner) {
            Log.d("QUERY", "products: ${it.size}")
            productAdapter.submitList(it)
            refreshLayout.isRefreshing = false
        }

        //有点多余，其实只是需要第一次显示
        productListViewModel.query.observe(viewLifecycleOwner) {
            Log.d("QUERY", "onActivityCreated: $it")
        }

        et_product.setText(productListViewModel.query.value)
        refreshLayout.isRefreshing = true

        btn_search.setOnClickListener {
            //存储并搜索值
            refreshLayout.isRefreshing = true
            productListViewModel.setQuery(et_product.text.toString().trim())
        }


//        et_product.doAfterTextChanged {
//            productListViewModel.setQuery(it.toString().trim())
//        }

    }

    class ProductListAdapter : ListAdapter<Product, ProductViewHolder>(DIFF_CALLBACK) {
        companion object {
            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ProductViewHolder(inflater.inflate(R.layout.item_product_list, parent,false))
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bindTo(getItem(position))
        }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindTo(product: Product) {
            itemView.product_id.text = "product Id:${product.id}"
            itemView.product_name.text = product.name
            itemView.setOnClickListener {
                Toast.makeText(itemView.context,"${product.id} clicked",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
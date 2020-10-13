package com.showdy.viewmodel

import androidx.lifecycle.*

/**
 * Created by <b>Showdy</b> on 2020/10/13 11:29
 *  Product List ViewModel
 */

class ProductListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        const val KEY_QUERY = "query"
    }

    //搜索框字符串存储
    val query: MutableLiveData<String> = savedStateHandle.getLiveData(KEY_QUERY, "")
    private val productRepository = ProductRepository()

    //根据query值，设置list product
    val products: LiveData<List<Product>> =
        query.switchMap {
            if (it.isNullOrBlank()) {
                productRepository.products
            } else {
                productRepository.searchProduct(it)
            }

        }

    //存储query值，并且当其变化是，products也会变化
    fun setQuery(query: String) {
        savedStateHandle.set(KEY_QUERY, query)
    }

    //刷新
    fun refresh() {
        query.value.let {
            query.value = it
        }
    }
}
package com.showdy.viewmodel

import androidx.lifecycle.LiveData

/**
 * Created by <b>Showdy</b> on 2020/10/13 11:34
 *
 */
interface IProductRepository {

    val products: LiveData<List<Product>>

    fun loadProduct(productId: Int): LiveData<Product>

    fun searchProduct(query: String): LiveData<List<Product>>
}
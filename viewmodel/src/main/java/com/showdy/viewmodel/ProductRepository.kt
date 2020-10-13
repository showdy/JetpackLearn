package com.showdy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

/**
 * Created by <b>Showdy</b> on 2020/10/13 11:34
 *
 */
class ProductRepository : IProductRepository {

//    private val _products = MediatorLiveData<List<Product>>()
//
//
//    init {
//        _products.addSource(createProduct()) {
//            //简化逻辑，直接通知
//            _products.postValue(it)
//        }
//    }


    override val products: LiveData<List<Product>>
        get() = createProduct()

    override fun loadProduct(productId: Int): LiveData<Product> {
        return liveData(Dispatchers.IO) {
            kotlinx.coroutines.delay(2000)
            productList.forEach {
                if (it.id == productId) {
                    emit(it)
                    return@liveData
                }
            }
        }
    }

    override fun searchProduct(query: String): LiveData<List<Product>> {
        return liveData(Dispatchers.IO) {
            kotlinx.coroutines.delay(3000)
            val result = mutableListOf<Product>()
            productList.forEach {
                if (query in it.name) {
                    result.add(it)
                }
            }
            emit(result)
        }
    }


    //--------模拟数据-----------


    private val productList = mutableListOf<Product>()


    private val productName = mutableListOf(
        "android", "ios", "kotlin", "netty", "shell",
        "c++", "c#", "javascript", "swift", "flutter"
    )


    private fun createProduct(): LiveData<List<Product>> {
        productList.clear()
        return liveData {
            for (index in 0 until 10) {
                productList.add(Product(index, productName[index], "App language", 100.0))
            }
            emit(productList)
        }
    }
}
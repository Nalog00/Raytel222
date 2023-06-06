package uz.raytel.raytel.domain.repository

import androidx.paging.PagingSource
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store

interface MainRepository {

    fun getProducts(id: Int): PagingSource<Int, Product>


    fun getStores(storeId: Int):PagingSource<Int, Store>
}
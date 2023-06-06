package uz.raytel.raytel.ui.main

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product

interface MainViewModel {


    val getRandomProductsFlow: StateFlow<Int>
    val randomProductsFlow: Flow<PagingData<Product>>
    val productsFlow: Flow<PagingResponse<Product>>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
     fun getRandomProducts()
    suspend fun getProducts(storeId: Int)
}

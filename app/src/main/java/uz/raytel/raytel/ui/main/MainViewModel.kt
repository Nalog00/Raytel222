package uz.raytel.raytel.ui.main

import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.auth.SignInPhone
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product

interface MainViewModel {

    val randomProductsFlow: Flow<GenericResponse<List<Product>>>
    val productsFlow: Flow<PagingResponse<Product>>
    val productViewedFlow: Flow<Any>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getRandomProducts()
    suspend fun getProducts(page: Int, storeId: Int, limit: Int)
    suspend fun productViewed(productId: Int)
}

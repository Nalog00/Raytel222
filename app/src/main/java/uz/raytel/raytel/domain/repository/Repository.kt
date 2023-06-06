package uz.raytel.raytel.domain.repository

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store

interface Repository {

    suspend fun uploadImage(image: MultipartBody.Part): Flow<ResultData<GenericResponse<List<UploadResponse>>>>

    suspend fun getRandomProductsWithoutLimit(): Flow<ResultData<Any>>

    suspend fun getStores(storeId: Int, page: Int, limit: Int = 30): Flow<ResultData<PagingResponse<Store>>>

    suspend fun getProducts(
        page: Int,
        storeId: Int,
        limit: Int = 50
    ): Flow<ResultData<PagingResponse<Product>>>

    suspend fun getDetails(): Flow<ResultData<GenericResponse<ConfirmData>>>

     fun productViewed(productId: Int): Flow<ResultData<Any>>
}

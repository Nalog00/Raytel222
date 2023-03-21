package uz.raytel.raytel.domain.repository

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.auth.SignInPhone
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store

interface Repository {

    suspend fun signInWithDeviceId(signInDeviceId: SignInDeviceId): Flow<ResultData<AuthResponse>>

    suspend fun signInWithPhoneNumber(signInPhone: SignInPhone): Flow<ResultData<AuthResponse>>

    suspend fun uploadImage(image: MultipartBody.Part): Flow<ResultData<GenericResponse<List<UploadResponse>>>>

    suspend fun signUp(signUpPhone: SignUpPhone): Flow<ResultData<AuthResponse>>

    suspend fun getRandomProducts(): Flow<ResultData<GenericResponse<List<Product>>>>

    suspend fun getStores(storeId: Int, page: Int, limit: Int = 30): Flow<ResultData<PagingResponse<Store>>>

    suspend fun getProducts(
        page: Int,
        storeId: Int,
        limit: Int = 50
    ): Flow<ResultData<PagingResponse<Product>>>

    suspend fun getDetails(): Flow<ResultData<GenericResponse<ConfirmData>>>

    suspend fun productViewed(productId: Int): Flow<ResultData<Any>>
}

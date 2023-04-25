package uz.raytel.raytel.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.auth.SignInPhone
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store

interface ApiService {

    @POST("api/signIn/mobile/device")
    suspend fun signInDeviceId(
        @Body signInDeviceId: SignInDeviceId
    ): Response<AuthResponse>

    @POST("api/signIn/mobile/phone")
    suspend fun signInPhoneNumber(
        @Body signInPhone: SignInPhone
    ): Response<AuthResponse>

    @POST("api/signUp/mobile/phone")
    suspend fun signUp(
        @Body signUpPhone: SignUpPhone
    ): Response<AuthResponse>

    @[Multipart POST("api/files")]
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<GenericResponse<List<UploadResponse>>>

    @GET("api/products/random")
    suspend fun getRandomProducts(): Response<GenericResponse<List<Product>>>

    @GET("api/products/random/without-limit")
    suspend fun getRandomProductsWithoutLimit(): Response<GenericResponse<List<Product>>>

    @GET("api/stores")
    suspend fun getStores(
        @Query("store_id") storeId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PagingResponse<Store>>

    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("store_id") storeId: Int,
        @Query("limit") limit: Int,
    ): Response<PagingResponse<Product>>

    @GET("api/settings")
    suspend fun getDetails(): Response<GenericResponse<ConfirmData>>

    @POST("api/products/view/{id}")
    suspend fun productViewed(
        @Path("id") productId: Int
    ): Response<Any>
}

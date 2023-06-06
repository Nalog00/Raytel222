package uz.raytel.raytel.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import uz.raytel.raytel.data.remote.auth.*
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.data.remote.paging.PagingResponse
import uz.raytel.raytel.data.remote.product.Product
import uz.raytel.raytel.data.remote.store.Store

interface ApiService {
    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @POST("api/signIn/mobile/device")
    suspend fun signInDeviceId(
        @Body signInDeviceId: SignInDeviceId
    ): Response<AuthResponse>


    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @POST("api/sms/sent")
    suspend fun sendSms(
        @Body sendSmsData: SendSmsData
    ): Response<SendSmsResponseData>

    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @POST("/api/sms/check")
    suspend fun checkSmsCode(@Body body: ConfirmSmsRequestData): Response<GenericResponse<ConfirmSmsResponseData>>

    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @POST("api/new-payment")
    suspend fun newPayment(
        @Body signUpPhone: NewPaymentRequestData
    ): Response<GenericResponse<NewPaymentResponseData>>


    @[Multipart POST("api/files")]
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<GenericResponse<List<UploadResponse>>>

    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @GET("api/products/random")
    suspend fun getRandomProducts(@Query("page") page: Int): Response<PagingResponse<Product>>


    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @GET("api/products/random/without-limit")
    suspend fun getRandomProductsWithoutLimit(@Query("page") page: Int): Response<PagingResponse<Product>>


    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @GET("api/stores")
    suspend fun getStores(
        @Query("store_id") storeId: Int, @Query("page") page: Int, @Query("limit") limit: Int
    ): Response<PagingResponse<Store>>

    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("store_id") storeId: Int,
        @Query("limit") limit: Int,
    ): Response<PagingResponse<Product>>

    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @GET("api/settings")
    suspend fun getDetails(): Response<GenericResponse<ConfirmData>>


    @Headers(
        "Accept: application/json", "Content-Type: application/json"
    )
    @POST("api/products/view/{id}")
    suspend fun productViewed(
        @Path("id") productId: Int
    ): Response<Any>


    @GET("api/getme")
    suspend fun getInfoAboutMe(): Response<GetInfoAboutMeData>
}

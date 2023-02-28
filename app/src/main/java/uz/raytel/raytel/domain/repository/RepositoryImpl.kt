package uz.raytel.raytel.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.ApiService
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.auth.SignInPhone
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun signInWithDeviceId(signInDeviceId: SignInDeviceId) = flow {
        emit(ResultData.Loading)

        val response = apiService.signInDeviceId(signInDeviceId)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun signInWithPhoneNumber(signInPhone: SignInPhone) = flow {
        emit(ResultData.Loading)

        val response = apiService.signInPhoneNumber(signInPhone)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun signUp(signUpPhone: SignUpPhone) = flow {
        emit(ResultData.Loading)

        val response = apiService.signUp(signUpPhone)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun uploadImage(image: MultipartBody.Part) = flow {
        emit(ResultData.Loading)

        val response = apiService.uploadImage(image)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getStores(
        storeId: Int,
        page: Int,
        limit: Int
    ) = flow {
        emit(ResultData.Loading)

        val response = apiService.getStores(storeId, page, limit)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getRandomProducts() = flow {
        emit(ResultData.Loading)

        val response = apiService.getRandomProducts()

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getProducts(
        page: Int,
        storeId: Int,
        limit: Int
    ) = flow {
        emit(ResultData.Loading)

        val response = apiService.getProducts(page, storeId, limit)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getDetails() = flow {
        emit(ResultData.Loading)

        val response = apiService.getDetails()

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)
}

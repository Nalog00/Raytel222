package uz.raytel.raytel.domain.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.ApiService
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*
import uz.raytel.raytel.domain.repository.Repository
import uz.raytel.raytel.utils.getErrorMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {
    override suspend fun uploadImage(image: MultipartBody.Part) = flow {
        emit(ResultData.Loading)

        val response = apiService.uploadImage(image)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getStores(
        storeId: Int, page: Int, limit: Int
    ) = flow {
        emit(ResultData.Loading)

        val response = apiService.getStores(storeId, page, limit)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getRandomProductsWithoutLimit() = flow {
        emit(ResultData.Loading)

        val response = apiService.getRandomProductsWithoutLimit(1)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getProducts(
        page: Int, storeId: Int, limit: Int
    ) = flow {
        emit(ResultData.Loading)

        val response = apiService.getProducts(page, storeId, limit)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
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
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override fun productViewed(productId: Int) = flow {
        emit(ResultData.Loading)

        val response = apiService.productViewed(productId)


        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)
}


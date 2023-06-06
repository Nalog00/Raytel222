package uz.raytel.raytel.domain.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.ApiService
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.domain.repository.AuthRepository
import uz.raytel.raytel.utils.getErrorMessage
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(private val apiService: ApiService) : AuthRepository {
    override suspend fun sendSmsCode(signInPhone: SendSmsData) = flow {
        emit(ResultData.Loading)
        val response = apiService.sendSms(signInPhone)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun confirmSms(body: ConfirmSmsRequestData) = flow {
        emit(ResultData.Loading)
        val response = apiService.checkSmsCode(body)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!.data))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun signInWithDeviceId(signInDeviceId: SignInDeviceId) = flow {
        emit(ResultData.Loading)

        val response = apiService.signInDeviceId(signInDeviceId)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun newPayment(newPaymentRequestData: NewPaymentRequestData) = flow {
        emit(ResultData.Loading)

        val response = apiService.newPayment(newPaymentRequestData)

        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!.data))
        } else {
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getInfoAboutMe() = flow {
        val response = apiService.getInfoAboutMe()
        if (response.isSuccessful){
            emit(ResultData.Success(response.body()!!))
        }else{
            emit(ResultData.Message(getErrorMessage(response.errorBody()!!.charStream())))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

}
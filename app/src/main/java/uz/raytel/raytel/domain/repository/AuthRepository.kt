package uz.raytel.raytel.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*

interface AuthRepository {


    suspend fun sendSmsCode(signInPhone: SendSmsData): Flow<ResultData<SendSmsResponseData>>


    suspend fun confirmSms(body: ConfirmSmsRequestData): Flow<ResultData<ConfirmSmsResponseData>>


    suspend fun signInWithDeviceId(signInDeviceId: SignInDeviceId): Flow<ResultData<AuthResponse>>


    suspend fun newPayment(newPaymentRequestData: NewPaymentRequestData): Flow<ResultData<NewPaymentResponseData>>


    suspend fun getInfoAboutMe(): Flow<ResultData<GetInfoAboutMeData>>
}
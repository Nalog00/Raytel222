package uz.raytel.raytel.ui.register

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*

interface RegisterViewModel {
    val sendSmsCodeFlow: Flow<SendSmsResponseData>
    val getMeFlow: Flow<GetInfoAboutMeData>
    val newPaymentSuccessFlow: Flow<NewPaymentResponseData>
    val signInDeviceIdFlow: Flow<AuthResponse>
    val confirmSmsCode: Flow<ConfirmSmsResponseData>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun sendSmsCode(body: SendSmsData)


    suspend fun confirmSmsCode(body: ConfirmSmsRequestData)

    suspend fun newPayment(body: NewPaymentRequestData)

    suspend fun signInDeviceId(body: SignInDeviceId)


    suspend fun getMe()
}

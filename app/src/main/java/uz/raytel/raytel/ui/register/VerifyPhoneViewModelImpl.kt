package uz.raytel.raytel.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.*
import uz.raytel.raytel.di.utils.UnauthorisedException
import uz.raytel.raytel.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModelImpl @Inject constructor(
    private val repo: AuthRepository,
    private val localStorage: LocalStorage
) : RegisterViewModel, ViewModel() {
    override val sendSmsCodeFlow = MutableSharedFlow<SendSmsResponseData>()
    override val getMeFlow = MutableSharedFlow<GetInfoAboutMeData>()
    override val newPaymentSuccessFlow = MutableSharedFlow<NewPaymentResponseData>()
    override val signInDeviceIdFlow = MutableSharedFlow<AuthResponse>()
    override val confirmSmsCode = MutableSharedFlow<ConfirmSmsResponseData>()
    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun sendSmsCode(body: SendSmsData) {
        repo.sendSmsCode(body).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    sendSmsCodeFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun confirmSmsCode(body: ConfirmSmsRequestData) {
        repo.confirmSms(body).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    confirmSmsCode.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun newPayment(body: NewPaymentRequestData) {
        repo.newPayment(body).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    newPaymentSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun signInDeviceId(body: SignInDeviceId) {
        repo.signInWithDeviceId(body).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    signInDeviceIdFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun getMe() {
        repo.getInfoAboutMe().onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    getMeFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    loadingFlow.emit(false)
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    loadingFlow.emit(false)
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }


}

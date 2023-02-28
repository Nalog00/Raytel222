package uz.raytel.raytel.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignUpPhone
import uz.raytel.raytel.domain.repository.Repository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModelImpl @Inject constructor(
    private val repository: Repository
): RegisterViewModel, ViewModel() {
    override val signUpFlow = MutableSharedFlow<AuthResponse>()
    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun signUp(signUpPhone: SignUpPhone) {
        repository.signUp(signUpPhone).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    signUpFlow.emit(it.data)
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

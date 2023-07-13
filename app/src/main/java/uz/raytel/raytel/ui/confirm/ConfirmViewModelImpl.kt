package uz.raytel.raytel.ui.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.ResultData
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse
import uz.raytel.raytel.di.utils.UnauthorisedException
import uz.raytel.raytel.domain.repository.AuthRepository
import uz.raytel.raytel.domain.repository.Repository
import javax.inject.Inject

@HiltViewModel
class ConfirmViewModelImpl @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository,
    private val localStorage: LocalStorage
): ConfirmViewModel, ViewModel() {




    override val confirmDetailsFlow = MutableSharedFlow<GenericResponse<ConfirmData>>()
    override val uploadImageFlow = MutableSharedFlow<GenericResponse<List<UploadResponse>>>()
    override val loadingFlow = MutableSharedFlow<Boolean>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getDetails() {
        repository.getDetails().onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    confirmDetailsFlow.emit(it.data)
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

    override suspend fun uploadImage(image: MultipartBody.Part) {
        repository.uploadImage(image).onEach {
            when (it) {
                is ResultData.Loading -> {
                    loadingFlow.emit(true)
                }
                is ResultData.Success -> {
                    loadingFlow.emit(false)
                    uploadImageFlow.emit(it.data)
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

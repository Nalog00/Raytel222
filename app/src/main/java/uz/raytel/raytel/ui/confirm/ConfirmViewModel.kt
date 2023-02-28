package uz.raytel.raytel.ui.confirm

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import uz.raytel.raytel.data.remote.GenericResponse
import uz.raytel.raytel.data.remote.confirm.ConfirmData
import uz.raytel.raytel.data.remote.confirm.UploadResponse

interface ConfirmViewModel {

    val confirmDetailsFlow: Flow<GenericResponse<ConfirmData>>
    val uploadImageFlow: Flow<GenericResponse<List<UploadResponse>>>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getDetails()
    suspend fun uploadImage(image: MultipartBody.Part)
}

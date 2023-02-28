package uz.raytel.raytel.ui.login

import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignInDeviceId
import uz.raytel.raytel.data.remote.auth.SignInPhone

interface LoginViewModel {
    val signInDeviceIdFlow: Flow<AuthResponse>
    val signInPhoneFlow: Flow<AuthResponse>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun signInDeviceId(signInDeviceId: SignInDeviceId)
    suspend fun signInPhone(signInPhone: SignInPhone)
}

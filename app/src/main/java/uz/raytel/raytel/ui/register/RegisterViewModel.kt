package uz.raytel.raytel.ui.register

import kotlinx.coroutines.flow.Flow
import uz.raytel.raytel.data.remote.auth.AuthResponse
import uz.raytel.raytel.data.remote.auth.SignUpPhone

interface RegisterViewModel {
    val signUpFlow: Flow<AuthResponse>
    val loadingFlow: Flow<Boolean>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun signUp(signUpPhone: SignUpPhone)
}

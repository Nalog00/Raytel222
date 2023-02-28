package uz.raytel.raytel.di

import okhttp3.Interceptor
import okhttp3.Response
import uz.raytel.raytel.data.local.LocalStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaytelInterceptor @Inject constructor(
    private val localStorage: LocalStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${localStorage.token}")
            .build()
        return chain.proceed(request)
    }
}

package uz.raytel.raytel.di

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.di.utils.UnauthorisedException
import uz.raytel.raytel.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaytelInterceptor @Inject constructor(
    private val localStorage: LocalStorage, @ApplicationContext val context: Context

) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer ${localStorage.token}")
                .build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            localStorage.firstRun = true
            localStorage.token = ""
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
        return response
    }
}

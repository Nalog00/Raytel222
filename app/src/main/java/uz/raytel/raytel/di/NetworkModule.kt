package uz.raytel.raytel.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.raytel.raytel.app.App
import uz.raytel.raytel.data.local.LocalStorage
import uz.raytel.raytel.data.remote.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        private const val BASE_URL = "https://api.raytel.uz/"
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun providesRaytelInterceptor(localStorage: LocalStorage) = RaytelInterceptor(localStorage, App.INSTANCE)


    @[Provides Singleton]
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        raytelInterceptor: RaytelInterceptor,
    ): OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(raytelInterceptor)
        .build()

    @[Provides Singleton]
    fun providesRetrofitInstance(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @[Provides Singleton]
    fun getApiProvides(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}

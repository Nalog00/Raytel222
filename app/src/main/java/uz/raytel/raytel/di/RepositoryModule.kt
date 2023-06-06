package uz.raytel.raytel.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.raytel.raytel.domain.repository.AuthRepository
import uz.raytel.raytel.domain.repository.MainRepository
import uz.raytel.raytel.domain.repository.impl.MainRepositoryImpl
import uz.raytel.raytel.domain.repository.Repository
import uz.raytel.raytel.domain.repository.impl.AuthRepositoryImpl
import uz.raytel.raytel.domain.repository.impl.RepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    fun bindMainRepository(impl: MainRepositoryImpl): MainRepository

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

package uz.raytel.raytel.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.raytel.raytel.domain.repository.Repository
import uz.raytel.raytel.domain.repository.RepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository
}

package com.example.rickandmortymvvm.di

import com.example.rickandmortymvvm.core.util.DispatcherProvider
import com.example.rickandmortymvvm.data.api.RetrofitInstance
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiService(): RickAndMortyApiService = RetrofitInstance.rickAndMortyService

    @Singleton
    @Provides
    fun provideRepository(
        rickAndMortyApiService: RickAndMortyApiService
    ): Repository = Repository(rickAndMortyApiService)

    @Singleton
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {
        override val mainCD: CoroutineDispatcher
            get() = Dispatchers.Main
        override val ioCD: CoroutineDispatcher
            get() = Dispatchers.IO
        override val defaultCD: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfinedCD: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}
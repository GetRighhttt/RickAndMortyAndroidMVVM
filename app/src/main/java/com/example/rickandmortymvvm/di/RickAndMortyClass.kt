package com.example.rickandmortymvvm.di

import com.example.rickandmortymvvm.data.api.RetrofitInstance
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
object RickAndMortyClass {

    @Singleton
    @Provides
    fun provideRickAndMortyApiService(): RickAndMortyApiService = RetrofitInstance.retrofit
}
package com.example.rickandmortymvvm.di

import android.app.Application
import com.example.rickandmortymvvm.data.api.RetrofitInstance
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
class RickAndMortyClass: Application() {}
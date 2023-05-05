package com.example.rickandmortymvvm.data.api

import com.example.rickandmortymvvm.domain.model.RickAndMortyTotalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {

    @GET("character/")
    suspend fun searchAllCharacters(
        @Query("name") name: String,
        @Query("page") page: Int
    ): Response<RickAndMortyTotalResponse>
}
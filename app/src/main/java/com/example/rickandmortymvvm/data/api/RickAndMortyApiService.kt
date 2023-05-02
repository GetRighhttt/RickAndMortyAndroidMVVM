package com.example.rickandmortymvvm.data.api

import com.example.rickandmortymvvm.domain.model.RickAndMortyTotalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {

    @GET("characters")
    suspend fun searchAllCharacters(
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<RickAndMortyTotalResponse>
}
package com.example.rickandmortymvvm.data.api

import com.example.rickandmortymvvm.domain.model.InfoRM
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {

    @GET("characters")
    suspend fun getAllCharacters(
        @Query("page") page: Int
    ): Response<Any>

    @GET("characters")
    suspend fun filterCharactersByName(
        @Query("name") query: String
    ): Response<InfoRM>
}
package com.example.rickandmortymvvm.domain.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.rickandmortymvvm.core.util.Constants
import com.example.rickandmortymvvm.data.RMPagingSource
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val rickAndMortyApiService: RickAndMortyApiService
) {
    fun searchAllCharacters(query: String, gender: String) =
        Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RMPagingSource(rickAndMortyApiService, query, gender) }
        ).liveData

    fun searchMaleCharacters(query: String, gender: String = "male") = Pager(
        config = PagingConfig(
            pageSize = Constants.PAGE_SIZE,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { RMPagingSource(rickAndMortyApiService, query, gender) }
    ).liveData

    fun searchFemaleCharacters(query: String, gender: String = "female") = Pager(
        config = PagingConfig(
            pageSize = Constants.PAGE_SIZE,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { RMPagingSource(rickAndMortyApiService, query, gender) }
    ).liveData

    fun addCharacter() {
        // TODO: Pass in repository save method from Room Database
    }

    fun deleteCharacter() {
        // TODO: Pass in repository save method from Room Database
    }
}
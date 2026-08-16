package com.example.rickandmortymvvm.domain.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.rickandmortymvvm.core.util.Constants
import com.example.rickandmortymvvm.data.api.RMPagingSource
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import com.example.rickandmortymvvm.data.db.CharacterDAO
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val rickAndMortyApiService: RickAndMortyApiService,
    private val characterDAO: CharacterDAO
) {
    fun searchCharacters(query: String, gender: String) =
        Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                initialLoadSize = Constants.PAGE_SIZE,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RMPagingSource(rickAndMortyApiService, query, gender) }
        ).flow

    suspend fun executeAddCharacter(character: RickAndMorty) = withContext(Dispatchers.IO) {
        characterDAO.insert(character)
    }

    suspend fun executeDeleteCharacter(character: RickAndMorty) = withContext(Dispatchers.IO) {
        characterDAO.deleteCharacter(character)
    }

    fun executeDeleteAllCharacters() = characterDAO.deleteAll()
    fun executeGetSavedCharacters(): Flow<List<RickAndMorty>> = characterDAO.getAllCharacters()
}

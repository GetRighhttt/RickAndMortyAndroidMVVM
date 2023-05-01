package com.example.rickandmortymvvm.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmortymvvm.core.util.Constants
import com.example.rickandmortymvvm.data.api.RickAndMortyApiService
import com.example.rickandmortymvvm.domain.model.RickAndMorty

class PagingSource (
    private val apiService: RickAndMortyApiService
) : PagingSource<Int, RickAndMorty>() {

    override fun getRefreshKey(state: PagingState<Int, RickAndMorty>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>):
            androidx.paging.PagingSource.LoadResult<Int, RickAndMorty> {

        return try {
            val currentPage = params.key ?: Constants.STARTING_PAGE_INDEX
            val response = apiService.getAllCharacters(currentPage)
            val responseData = mutableListOf<RickAndMorty>()
            val data = response.body()?.results ?: emptyList()
            responseData.addAll(data)

            androidx.paging.PagingSource.LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            androidx.paging.PagingSource.LoadResult.Error(e)
        }

    }
}

package com.example.rickandmortymvvm.data.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmortymvvm.core.util.Constants
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import retrofit2.HttpException
import java.io.IOException

class RMPagingSource(
    private val apiService: RickAndMortyApiService,
    private val query: String,
    private val gender: String
) : PagingSource<Int, RickAndMorty>() {
    override suspend fun load(params: LoadParams<Int>):
            LoadResult<Int, RickAndMorty> {

        return try {
            val currentPage = params.key ?: Constants.STARTING_PAGE_INDEX
            val response = apiService.searchAllCharacters(query, currentPage, gender)
            val responseData = mutableListOf<RickAndMorty>()
            val results = response.body()?.results ?: emptyList()
            responseData.addAll(results)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = if (results.isEmpty()) null else currentPage.plus(1)
            )
        } catch (e: IOException) { // no internet connection, etc.
            LoadResult.Error(e)
        } catch (e: HttpException) { // http status code exception
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RickAndMorty>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

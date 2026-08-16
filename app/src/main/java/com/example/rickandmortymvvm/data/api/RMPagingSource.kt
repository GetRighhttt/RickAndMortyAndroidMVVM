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
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RickAndMorty> {
        return try {
            val currentPage = params.key ?: Constants.STARTING_PAGE_INDEX
            val response = apiService.searchAllCharacters(query, currentPage, gender)

            // This API uses 404 to represent a filter with no matching characters.
            if (response.code() == HTTP_NOT_FOUND) {
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }
            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val body = response.body()
                ?: return LoadResult.Error(IllegalStateException("Character response body was empty"))

            LoadResult.Page(
                data = body.results,
                prevKey = body.info.prev?.let { currentPage - 1 },
                nextKey = body.info.next?.let { currentPage + 1 }
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RickAndMorty>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}

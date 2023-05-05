package com.example.rickandmortymvvm.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RickAndMortyViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    val rickAndMortyResults = currentQuery.switchMap { queryString ->
        repository.searchAllCharacters(queryString).cachedIn(viewModelScope)
    }

    fun searchCharacters(query: String) = viewModelScope.launch {
        try {
            currentQuery.value = query
        } catch (e: HttpException) {
            Log.d("VIEW_MODEL", "${e.printStackTrace()}")
        }
    }
}
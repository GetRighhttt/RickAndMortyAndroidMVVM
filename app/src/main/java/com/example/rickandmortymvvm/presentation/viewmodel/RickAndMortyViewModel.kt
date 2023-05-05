package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.rickandmortymvvm.core.events.SearchEvent
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
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

    init {
        searchCharacters(DEFAULT_QUERY)
    }

    val rickAndMortyResults = currentQuery.switchMap { queryString ->
        repository.searchAllCharacters(queryString).cachedIn(viewModelScope)
    }

    private fun searchCharacters(query: String) = viewModelScope.launch {
        currentQuery.value = query
    }
}
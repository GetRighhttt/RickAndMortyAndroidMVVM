package com.example.rickandmortymvvm.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RickAndMortyViewModel @Inject constructor(
    private val repository: Repository,
    saved: SavedStateHandle // tries to saves position of recycler view when app is killed
) : ViewModel() {

    private val currentQuery = saved.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val maleGenderData = MutableLiveData(DEFAULT_QUERY)
    private val femaleGenderData = MutableLiveData(DEFAULT_QUERY)

    // using lambdas instead of methods
    val rickAndMortyResult = currentQuery.switchMap { queryString ->
        repository.searchAllCharacters(queryString, "").cachedIn(viewModelScope)
    }

    val maleGenderResult = maleGenderData.switchMap {
        repository.searchMaleCharacters("", "male").cachedIn(viewModelScope)
    }

    val femaleGenderResult = femaleGenderData.switchMap {
        repository.searchFemaleCharacters("", "female").cachedIn(viewModelScope)
    }

    val searchCharacterJob: (String) -> Job =
        { query: String ->
            viewModelScope.launch {
                try {
                    currentQuery.value = query
                } catch (e: HttpException) {
                    Log.d("VIEW_MODEL", "${e.printStackTrace()} - Could not find characters!")
                }
            }
        }

    companion object {
        private const val DEFAULT_QUERY = ""
        private const val CURRENT_QUERY = "current_query"
    }
}

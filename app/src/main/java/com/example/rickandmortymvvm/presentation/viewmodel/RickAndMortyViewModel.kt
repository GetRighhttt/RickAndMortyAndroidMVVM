package com.example.rickandmortymvvm.presentation.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

enum class CharacterGender(val apiValue: String) {
    ALL(""),
    MALE("male"),
    FEMALE("female")
}

@Parcelize
data class CharacterFilters(
    val query: String = "",
    val gender: CharacterGender = CharacterGender.ALL
) : Parcelable

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RickAndMortyViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val filters: StateFlow<CharacterFilters> =
        savedStateHandle.getStateFlow(FILTERS_KEY, CharacterFilters())

    // A new filter cancels the previous Pager. Cache the resulting stream, not each filter branch.
    val characters = filters.flatMapLatest { filters ->
        repository.searchCharacters(filters.query, filters.gender.apiValue)
    }.cachedIn(viewModelScope)

    fun searchCharacters(query: String) = updateFilters { it.copy(query = query.trim()) }
    fun filterByGender(gender: CharacterGender) = updateFilters { it.copy(gender = gender) }
    fun showAllCharacters() { savedStateHandle[FILTERS_KEY] = CharacterFilters() }

    private fun updateFilters(transform: (CharacterFilters) -> CharacterFilters) {
        savedStateHandle[FILTERS_KEY] = transform(filters.value)
    }

    companion object {
        private const val FILTERS_KEY = "character_filters"
    }
}

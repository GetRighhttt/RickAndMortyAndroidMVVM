package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _currentState = MutableLiveData<List<RickAndMorty>>()
    val currentState: LiveData<List<RickAndMorty>> get() = _currentState

    init {
        getAllSavedCharacters()
    }

    fun addCharacter(character: RickAndMorty) = viewModelScope.launch {
        repository.executeAddCharacter(character)
    }

    fun deleteCharacter(character: RickAndMorty) = viewModelScope.launch {
        repository.executeDeleteCharacter(character)
    }

    private fun getAllSavedCharacters() = viewModelScope.launch {
        repository.executeGetSavedCharacters().collectLatest { _currentState.value = it }
    }

}
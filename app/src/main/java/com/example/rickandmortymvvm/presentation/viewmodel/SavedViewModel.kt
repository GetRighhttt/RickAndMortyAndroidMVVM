package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    fun addCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        repository.executeAddCharacter(character)
    }

    fun deleteCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        repository.executeDeleteCharacter(character)
    }

    /*
    Am not passing in a Dispatcher here for viewModelScope because we need this method to
    be observed on the main thread, not just CALLED. The other methods are just called in the
    activity, but they are not observed there.
     */
    private fun getAllSavedCharacters() = viewModelScope.launch {
        repository.executeGetSavedCharacters().collectLatest { _currentState.value = it }
    }

}
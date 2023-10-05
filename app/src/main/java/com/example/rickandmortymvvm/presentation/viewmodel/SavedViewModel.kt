package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // Collecting flow data in LiveData variable
    private val _currentState = MutableLiveData<List<RickAndMorty>>()
    val currentState: LiveData<List<RickAndMorty>> get() = _currentState

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /*
    Adding operator methods with LiveData.
     */
    private operator fun MutableLiveData<Boolean>.invoke(state: Boolean?) {
        _isLoading.postValue(state)
    }

    init {
        // initialize the view model with list of characters saved from Detail Activity
        getAllSavedCharacters()
    }

    fun addCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading(true)
        delay(1000)
        repository.executeAddCharacter(character)
        _isLoading(false)
    }

    fun deleteCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading(true)
        delay(1000)
        repository.executeDeleteCharacter(character)
        _isLoading(false)
    }

    fun deleteAllCharacters() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading(true)
        repository.executeDeleteAllCharacters()
        _isLoading(false)
    }

    /*
    Am not passing in a Dispatcher here for viewModelScope because we need this method to
    be observed on the main thread, not just CALLED. The other methods are just called in the
    activity, but they are not observed there.
     */
    private fun getAllSavedCharacters() = viewModelScope.launch {
        _isLoading(true)
        delay(1000)
        repository.executeGetSavedCharacters().collectLatest {
            _currentState.postValue(it)
            _isLoading(false)
        }
    }
}
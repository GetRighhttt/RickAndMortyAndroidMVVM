package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // operator invoke methods
    private operator fun MutableLiveData<Boolean>.invoke(state: Boolean) = _isLoading.postValue(state)
    private suspend operator fun Repository.invoke(character: RickAndMorty) = repository.executeAddCharacter(character)

    init {
        // fault tolerance
        _isLoading(false)
    }

    fun addCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading(true)
        repository(character)
        _isLoading(false)
    }
}
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

    init {
        // fault tolerance
        _isLoading.postValue(false)
    }

    fun addCharacter(character: RickAndMorty) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.postValue(true)
        repository.executeAddCharacter(character)
        _isLoading.postValue(false)
    }
}
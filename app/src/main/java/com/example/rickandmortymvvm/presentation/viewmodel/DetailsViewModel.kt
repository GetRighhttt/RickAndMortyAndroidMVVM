package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmortymvvm.domain.model.RickAndMorty
import com.example.rickandmortymvvm.domain.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor (
    private val repository: Repository
    ) : ViewModel() {

    private val _currentState = MutableLiveData<String>()
    val currentState: LiveData<String> get() = _currentState

    fun addCharacter(characterId: Int) {
        // TODO: Call repository method to save character to database with _currentState
    }
}
package com.example.rickandmortymvvm.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmortymvvm.core.events.Resource
import kotlinx.coroutines.flow.MutableStateFlow

class DetailsViewModel : ViewModel() {

    private val _currentState = MutableLiveData<String>()
}
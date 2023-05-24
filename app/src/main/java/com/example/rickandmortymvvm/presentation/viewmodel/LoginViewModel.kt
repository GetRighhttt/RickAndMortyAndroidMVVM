package com.example.rickandmortymvvm.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    companion object {
        const val LOGIN_VIEW_MODEL = "LOGIN_VIEW_MODEL"
    }

    init {
        _loginState.postValue(false)
        Log.d(LOGIN_VIEW_MODEL, "LoginViewModel started.")
    }

    fun determineLoginState(name: String) {
        if (name.isNotEmpty()) _loginState.postValue(true) else _loginState.postValue(false)
    }
}
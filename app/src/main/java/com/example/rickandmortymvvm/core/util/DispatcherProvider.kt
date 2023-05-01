package com.example.rickandmortymvvm.core.util

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val mainCD: CoroutineDispatcher
    val ioCD: CoroutineDispatcher
    val defaultCD: CoroutineDispatcher
    val unconfinedCD: CoroutineDispatcher
}
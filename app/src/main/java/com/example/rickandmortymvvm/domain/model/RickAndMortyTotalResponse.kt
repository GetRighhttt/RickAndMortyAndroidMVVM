package com.example.rickandmortymvvm.domain.model

import android.icu.text.IDNA.Info

data class RickAndMortyTotalResponse(
    val info: Info,
    val results: List<RickAndMorty>
)

package com.example.rickandmortymvvm.domain.model

data class RickAndMortyTotalResponse(
    val info: RickAndMortyPageInfo,
    val results: List<RickAndMorty>
)

data class RickAndMortyPageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

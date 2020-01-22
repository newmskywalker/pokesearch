package com.mateoj.pokesearch.api

data class PokemonResultList(val count: Int, val next: String, val results: List<PokemonResult>)

data class PokemonResult(val name: String)
package com.mateoj.pokesearch.repository

import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.Specie
import com.mateoj.pokesearch.util.Result

interface PokemonRepository {
    fun getPaginatedList() : Listing<Pokemon>

    suspend fun getPokemonByName(name: String) : Result<Pokemon>

    suspend fun getSpecieByName(name: String) : Result<Specie>
}
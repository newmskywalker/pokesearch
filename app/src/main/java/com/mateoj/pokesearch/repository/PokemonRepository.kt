package com.mateoj.pokesearch.repository

import com.mateoj.pokesearch.api.Pokemon

interface PokemonRepository {
    fun getAll() : Listing<Pokemon>
}
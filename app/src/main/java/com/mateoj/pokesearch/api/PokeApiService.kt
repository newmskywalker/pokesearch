package com.mateoj.pokesearch.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val POKEMON_PATH = "v2/pokemon"
private const val SPECIES_PATH = "v2/pokemon-species"

interface PokeApiService {

    @GET(POKEMON_PATH)
    suspend fun getPokemonList(@Query("limit") limit: Int? = null, @Query("offset") offset: Int? = null) : PokemonResultList

    @GET("$POKEMON_PATH/{name}")
    suspend fun getPokemonByName(@Path("name") name: String) : Pokemon

    @GET("$SPECIES_PATH/{name}")
    suspend fun getSpecieByName(@Path("name") name: String) : Specie
}
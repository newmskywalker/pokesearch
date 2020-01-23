package com.mateoj.pokesearch.db

import androidx.room.*

@Dao
interface PokemonSearchSuggestionsDao {
    @Query("SELECT * FROM pokemon WHERE name LIKE :term")
    suspend fun getSuggestions(term: String) : List<PokemonSuggestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pokemons: List<PokemonSuggestion>)
}
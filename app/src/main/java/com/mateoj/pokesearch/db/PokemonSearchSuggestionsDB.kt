package com.mateoj.pokesearch.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PokemonSuggestion::class], version = 2, exportSchema = false)
abstract class PokemonSearchSuggestionsDB : RoomDatabase() {
    abstract fun pokemonDao() : PokemonSearchSuggestionsDao
}
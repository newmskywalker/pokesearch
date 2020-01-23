package com.mateoj.pokesearch.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.db.PokemonSearchSuggestionsDB

class PokemonListDataSourceFactory(private val pokeApiService: PokeApiService,
                                   private val searchSuggestionsDb: PokemonSearchSuggestionsDB) : DataSource.Factory<String, Pokemon>() {
    private val _sourceLiveData = MutableLiveData<PokemonListDataSource>()
    val sourceLiveData : LiveData<PokemonListDataSource>
        get() = _sourceLiveData

    override fun create(): DataSource<String, Pokemon> {
        val source = PokemonListDataSource(searchSuggestionsDb, pokeApiService)
        _sourceLiveData.postValue(source)
        return source
    }
}
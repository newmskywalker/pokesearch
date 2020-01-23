package com.mateoj.pokesearch.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.api.Pokemon
import kotlinx.coroutines.CoroutineScope

class PokemonListDataSourceFactory(val pokeApiService: PokeApiService) : DataSource.Factory<String, Pokemon>() {
    private val _sourceLiveData = MutableLiveData<PokemonListDataSource>()
    val sourceLiveData : LiveData<PokemonListDataSource>
        get() = _sourceLiveData

    override fun create(): DataSource<String, Pokemon> {
        val source = PokemonListDataSource(pokeApiService)
        _sourceLiveData.postValue(source)
        return source
    }
}
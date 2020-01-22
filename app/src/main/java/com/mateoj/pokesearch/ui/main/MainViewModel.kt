package com.mateoj.pokesearch.ui.main

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.repository.NetworkState
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.repository.Status
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class MainViewModel : ViewModel(), KoinComponent {
    private val repository: PokemonRepository by inject { parametersOf(viewModelScope) }
    private val pagingList = repository.getAll()

    val pokemonList : LiveData<PagedList<Pokemon>>
        get() = pagingList.pagedList

    val initialLoading: LiveData<Boolean>
        get() = Transformations.map(pagingList.refreshState){it.status == Status.RUNNING}

    val initialLoaded: LiveData<Boolean>
        get() = Transformations.map(pagingList.refreshState){it.status == Status.SUCCESS}
}

package com.mateoj.pokesearch.ui.main

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.repository.Status

class MainViewModel(repository: PokemonRepository) : ViewModel() {
    private val pagingList = repository.getPaginatedList()

    val pokemonList : LiveData<PagedList<Pokemon>>
        get() = pagingList.pagedList

    val initialLoading: LiveData<Boolean>
        get() = Transformations.map(pagingList.refreshState){ it.status == Status.RUNNING }

    val initialLoaded: LiveData<Boolean>
        get() = Transformations.map(pagingList.refreshState){ it.status == Status.SUCCESS }

    val error : LiveData<Boolean>
        get() = Transformations.map(pagingList.networkState){ it.status == Status.FAILED }

    fun refresh() {
        pagingList.refresh()
    }

    fun retry() {
        pagingList.retry()
    }

    override fun onCleared() {
        super.onCleared()
        pagingList.cancelCoroutines()
    }
}

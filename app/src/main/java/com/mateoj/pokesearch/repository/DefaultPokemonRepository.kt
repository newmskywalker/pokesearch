package com.mateoj.pokesearch.repository

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mateoj.pokesearch.api.Pokemon
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

private const val DEFAULT_PAGE_SIZE = 5
private const val DEFAULT_FIRST_PAGE_SIZE = DEFAULT_PAGE_SIZE

class DefaultPokemonRepository(scope: CoroutineScope) : PokemonRepository, KoinComponent {
    private val sourceFactory: PokemonListDataSourceFactory by inject { parametersOf(scope) }

    override fun getAll(): Listing<Pokemon> {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(DEFAULT_PAGE_SIZE)
            .setInitialLoadSizeHint(DEFAULT_FIRST_PAGE_SIZE)
            .build()

        val livePagedList = LivePagedListBuilder<String, Pokemon>(sourceFactory,  config)

        return Listing(
            pagedList = livePagedList.build(),
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData){it.networkState},
            refreshState = Transformations.switchMap(sourceFactory.sourceLiveData){it.initialLoad},
            retry = {sourceFactory.sourceLiveData.value?.retryAllFailed() },
            refresh = {sourceFactory.sourceLiveData.value?.invalidate()}
        )
    }
}
package com.mateoj.pokesearch.repository

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.Specie
import com.mateoj.pokesearch.util.Result

private const val DEFAULT_PAGE_SIZE = 10
private const val DEFAULT_FIRST_PAGE_SIZE = DEFAULT_PAGE_SIZE

class DefaultPokemonRepository(private val apiService: PokeApiService,
                               private val sourceFactory: PokemonListDataSourceFactory) : PokemonRepository {

    override suspend fun getSpecieByName(name: String): Result<Specie> =
        try {
            Result.Success(apiService.getSpecieByName(name))
        } catch (e: Throwable) {
            Result.Error(e)
        }

    override suspend fun getPokemonByName(name: String): Result<Pokemon> =
        try {
            Result.Success(apiService.getPokemonByName(name))
        } catch (e: Throwable) {
            Result.Error(e)
        }

    override fun getPaginatedList(): Listing<Pokemon> {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(DEFAULT_PAGE_SIZE)
            .setInitialLoadSizeHint(DEFAULT_FIRST_PAGE_SIZE)
            .build()

        val livePagedList = LivePagedListBuilder(sourceFactory,  config)

        return Listing(
            pagedList = livePagedList.build(),
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData){it.networkState},
            refreshState = Transformations.switchMap(sourceFactory.sourceLiveData){it.initialLoad},
            retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
            refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
            cancelCoroutines = { sourceFactory.sourceLiveData.value?.cancelCoroutines() }
        )
    }
}
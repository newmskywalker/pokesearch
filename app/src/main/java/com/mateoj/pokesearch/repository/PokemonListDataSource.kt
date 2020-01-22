package com.mateoj.pokesearch.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.PokemonResultList
import kotlinx.coroutines.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PokemonListDataSource(
    private val coroutineScope: CoroutineScope,
    private val apiService: PokeApiService,
    private val retryExecutor : Executor = Executors.newSingleThreadExecutor()
) : PageKeyedDataSource<String, Pokemon>() {

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    private fun pokemonList(result: PokemonResultList) : List<Pokemon> = runBlocking {
        result.results.map {
            async { apiService.getPokemonByName(it.name) }
        }.awaitAll()
    }

    private fun postLoading() {
        networkState.postValue(NetworkState.LOADING)
    }

    private fun postError(msg: String? = null) {
        networkState.postValue(NetworkState.error(msg))
    }

    private fun postLoaded() {
        networkState.postValue(NetworkState.LOADED)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Pokemon>
    ) {
        initialLoad.postValue(NetworkState.LOADING)
        postLoading()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val result = apiService.getPokemonList(limit = params.requestedLoadSize)
                callback.onResult(pokemonList(result), 0, result.count, null, result.next)
                postLoaded()
                initialLoad.postValue(NetworkState.LOADED)
            } catch (e: Throwable) {
                callback.onError(e)
                postError(e.message)
                initialLoad.postValue(NetworkState.error(e.message))
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Pokemon>) {
        postLoading()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                with(Uri.parse(params.key)) {
                    val offset = getQueryParameter("offset")
                    val limit = getQueryParameter("limit")
                    val result = apiService.getPokemonList(limit = limit?.toInt(), offset = offset?.toInt())
                    callback.onResult(pokemonList(result),  result.next)
                    postLoaded()
                }

            } catch (e: Throwable) {
                callback.onError(e)
                postError(e.message)
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Pokemon>) {
        // not used
    }

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null

        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }
}
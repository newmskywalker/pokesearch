package com.mateoj.pokesearch.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.PokemonResultList
import com.mateoj.pokesearch.db.PokemonSearchSuggestionsDB
import com.mateoj.pokesearch.db.PokemonSuggestion
import kotlinx.coroutines.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PokemonListDataSource(
    private val searchSuggestionsDb: PokemonSearchSuggestionsDB,
    private val apiService: PokeApiService,
    private val retryExecutor : Executor = Executors.newSingleThreadExecutor()
) : PageKeyedDataSource<String, Pokemon>() {

    private var retry: (() -> Any)? = null

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

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
        scope.launch {
            try {
                val result = apiService.getPokemonList(limit = params.requestedLoadSize)
                val list = result.results.map { async { apiService.getPokemonByName(it.name) } }
                saveToDb(result)
                callback.onResult(list.awaitAll(), 0, result.count, null, result.next)
                postLoaded()
                initialLoad.postValue(NetworkState.LOADED)
            } catch (e: Throwable) {
                retry = { loadInitial(params, callback) }
                postError(e.message)
                e.printStackTrace()
                initialLoad.postValue(NetworkState.error(e.message))
            }
        }
    }

    private fun saveToDb(result: PokemonResultList) {
        val list = mutableListOf<PokemonSuggestion>()
        result.results.forEach { p ->
            val id = Uri.parse(p.url).lastPathSegment
            id?.toIntOrNull()?.let {
                list.add(PokemonSuggestion(id = it, name = p.name))
            }
        }

        searchSuggestionsDb.pokemonDao().insertAll(list)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Pokemon>) {
        postLoading()
        scope.launch {
            try {
                with(Uri.parse(params.key)) {
                    val offset = getQueryParameter("offset")
                    val limit = getQueryParameter("limit")
                    val result = apiService.getPokemonList(limit = limit?.toInt(), offset = offset?.toInt())
                    val list = result.results.map { async { apiService.getPokemonByName(it.name) } }

                    saveToDb(result)
                    callback.onResult(list.awaitAll(),  result.next)
                    postLoaded()
                }

            } catch (e: Throwable) {
                retry = { loadAfter(params, callback) }
                e.printStackTrace()
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

    fun cancelCoroutines() {
        scope.cancel()
    }
}
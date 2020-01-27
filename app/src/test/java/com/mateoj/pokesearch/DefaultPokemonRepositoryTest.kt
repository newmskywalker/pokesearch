package com.mateoj.pokesearch

import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.repository.DefaultPokemonRepository
import com.mateoj.pokesearch.repository.NetworkState
import com.mateoj.pokesearch.util.Result
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.koin.test.KoinTest
import java.lang.Exception
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
class DefaultPokemonRepositoryTest : KoinTest {

    @Test
    fun `Getting a species works`() {
        val apiService : PokeApiService = mock()

        runBlockingTest {
            whenever(apiService.getSpecieByName(any())).thenReturn(mock())
            val repo = DefaultPokemonRepository(apiService, mock())
            Assert.assertTrue(repo.getSpecieByName("pika") is Result.Success)
        }
    }

    @Test
    fun `Getting a pokemon fails`() {
        val apiService : PokeApiService = mock()

        runBlockingTest {
            whenever(apiService.getPokemonByName(any())).thenAnswer { throw  Exception("Fail")}
            val repo = DefaultPokemonRepository(apiService, mock())
            Assert.assertTrue(repo.getPokemonByName("pika") is Result.Error)
        }
    }

    @Test
    fun `Getting a species fails`() {
        val apiService : PokeApiService = mock()

        runBlockingTest {
            whenever(apiService.getSpecieByName(any())).thenAnswer { throw  Exception("Fail")}
            val repo = DefaultPokemonRepository(apiService, mock())
            Assert.assertTrue(repo.getSpecieByName("pika") is Result.Error)
        }
    }
}


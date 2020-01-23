package com.mateoj.pokesearch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.Specie
import com.mateoj.pokesearch.api.Species
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.ui.detail.PokemonDetailViewModel
import com.mateoj.pokesearch.util.Result
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
class PokemonDetailViewModelTest {
    @get:Rule
    val instantTaskExecutorRule  = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = TestCoroutineRule()

    @Test
    fun `Test pokemon and species updates`() = coroutinesRule.runBlockingTest {
        val repository : PokemonRepository = mock()
        val mockedPokemon : Pokemon = mock()
        whenever(mockedPokemon.species).thenReturn(Species("pikachu", ""))
        whenever(repository.getPokemonByName(any())).thenReturn(Result.Success(mockedPokemon))
        whenever(repository.getSpecieByName(any())).thenReturn(Result.Success(mock()))

        val viewModel = PokemonDetailViewModel(repository)

        val mockPokemonObserver : Observer<Pokemon?> = mock()
        val mockSpecieObserver : Observer<Specie?> = mock()

        viewModel.pokemonDetails.observeForever(mockPokemonObserver)
        viewModel.pokemonSpecie.observeForever(mockSpecieObserver)
        viewModel.setPokemonName("pika")

        verify(mockPokemonObserver).onChanged(any())
        verify(mockSpecieObserver).onChanged(any())
    }

    @Test
    fun `Test error livedata updates`() = coroutinesRule.runBlockingTest() {
        val repository : PokemonRepository = mock()
        whenever(repository.getPokemonByName(any())).thenReturn(Result.Error(Exception("Fail")))
        val viewModel = PokemonDetailViewModel(repository)
        val mockObserver : Observer<Boolean> = mock()
        viewModel.hasError.observeForever(mockObserver)
        viewModel.setPokemonName("ffd")

        verify(mockObserver).onChanged(true)
    }
}
package com.mateoj.pokesearch.ui.detail

import android.graphics.Color
import androidx.lifecycle.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.Specie
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.util.Result
import kotlinx.coroutines.launch
import java.util.*

class PokemonDetailViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val rnd = Random()

    private val pokemonName = MutableLiveData<String>()

    private val _pokemonSpecie = MutableLiveData<Specie?>()
    val pokemonSpecie : LiveData<Specie?>
        get() = _pokemonSpecie

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean>
        get() = _isLoading

    private val _pokemonDetails = MutableLiveData<Pokemon?>()
    val pokemonDetails: LiveData<Pokemon?>
        get() = _pokemonDetails

    private val _hasError = MutableLiveData<Boolean>()
    val hasError : LiveData<Boolean>
        get() = _hasError

    val pokemonStatData: LiveData<BarData>
        get() = Transformations.map(_pokemonDetails) {
            it?.let {
                BarData(it.stats.mapIndexed { index, stat ->
                    BarDataSet(listOf(
                        BarEntry(index.toFloat(), stat.baseStat.toFloat())
                    ), stat.stat.name.replace("-", " ")).apply {
                        setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
                    }
                })
            }
        }

    private suspend fun getSpecie(specieName: String) {
        val specie = repository.getSpecieByName(specieName)
        when(specie) {
            is Result.Success -> {
                _pokemonSpecie.postValue(specie.data)
            }
        }
    }

    fun setPokemonName(name: String) {
        pokemonName.value = name
        _isLoading.value = true
        _hasError.value = false
        viewModelScope.launch {
            val pokemon = repository.getPokemonByName(name)
            _isLoading.value = false
            when(pokemon) {
                is Result.Success  -> {
                    getSpecie(pokemon.data.species.name)
                    _pokemonDetails.postValue(pokemon.data)
                }
                else -> {
                    _pokemonDetails.value = null
                    _hasError.value = true
                }
            }
        }
    }
}

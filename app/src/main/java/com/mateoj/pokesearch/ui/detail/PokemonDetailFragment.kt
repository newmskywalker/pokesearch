package com.mateoj.pokesearch.ui.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mateoj.pokesearch.R

import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.api.Specie
import com.mateoj.pokesearch.extensions.decaToMeter
import com.mateoj.pokesearch.extensions.hectoToKilo
import com.mateoj.pokesearch.extensions.load
import kotlinx.android.synthetic.main.pokemon_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap


class PokemonDetailFragment : Fragment() {

    private val colorMap : HashMap<String, Int> = hashMapOf(
        "fire" to Color.parseColor("#E65100"),
        "poison" to Color.parseColor("#4A148C"),
        "water" to Color.parseColor("#1565C0"),
        "grass" to Color.parseColor("#388E3C"),
        "electric" to Color.parseColor("#FBC02D"),
        "flying" to Color.parseColor("#0091EA")
    )

    companion object {
        fun newInstance() = PokemonDetailFragment()
    }

    private val detailViewModel: PokemonDetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_detail_fragment, container, false)
    }

    private fun showPokemonDetails(pokemon: Pokemon) {
        content.isVisible = true
        pokemon_error.isGone = true
        progress_bar.isGone = true

        pokemon_detail_image.load(pokemon.sprites.frontDefault)
        pokemon_detail_name.text = pokemon.name.capitalize()
        pokemon_detail_weight.text = getString(R.string.pokemon_weight, pokemon.weight.hectoToKilo())
        pokemon_detail_height.text = getString(R.string.pokemon_height, pokemon.height.decaToMeter())
        pokemon.types.firstOrNull()?.type?.name?.let {
            pokemon_detail_chip_1.chipBackgroundColor = ColorStateList.valueOf(colorMap[it] ?: Color.GRAY)
            pokemon_detail_chip_1.text = it
        } ?: run {
            pokemon_detail_chip_1.isGone = true
        }

        pokemon.types.getOrNull(1)?.type?.name?.let {
            pokemon_detail_chip_2.text = it
            pokemon_detail_chip_2.chipBackgroundColor = ColorStateList.valueOf(colorMap[it] ?: Color.GRAY)
        } ?: run {
            pokemon_detail_chip_2.isGone = true
        }
    }

    private fun showPokemonSpecieDetails(specie: Specie) {
        // TODO use a map so getting the correct language is faster
        pokemon_detail_description.text = specie.flavorTextEntries
            .firstOrNull { it.language.name == "en" }?.flavorText
    }

    private fun showError() {
        pokemon_error.isVisible = true
        content.isGone = true
        progress_bar.isGone = true
    }

    private fun showLoading() {
        progress_bar.isVisible = true
        content.isGone = true
        pokemon_error.isGone = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if(it) showLoading()
        })

        detailViewModel.pokemonDetails.observe(viewLifecycleOwner, Observer { pokemon ->
            pokemon?.let { showPokemonDetails(it) }
        })

        detailViewModel.hasError.observe(viewLifecycleOwner, Observer {
            if(it) showError()
        })

        detailViewModel.pokemonSpecie.observe(viewLifecycleOwner, Observer {specie ->
            specie?.let { showPokemonSpecieDetails(it) }
        })

        detailViewModel.pokemonStatData.observe(viewLifecycleOwner, Observer { data ->
            pokemon_stats_chart.data = data
            pokemon_stats_chart.setFitBars(true)
            pokemon_stats_chart.invalidate()
        })

        arguments?.let { detailViewModel.setPokemonName(PokemonDetailFragmentArgs.fromBundle(it).pokemonName)}
    }

}

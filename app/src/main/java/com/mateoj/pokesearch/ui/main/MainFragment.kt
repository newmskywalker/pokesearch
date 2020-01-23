package com.mateoj.pokesearch.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mateoj.pokesearch.R
import com.mateoj.pokesearch.ui.detail.PokemonDetailFragmentDirections
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val mainViewModel: MainViewModel by viewModel()
    private val adapter: PokemonListAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_recycler_view.adapter = adapter
        main_recycler_view.layoutManager = LinearLayoutManager(context)
        refresh_layout.setOnRefreshListener { mainViewModel.refresh() }
        try_again.setOnClickListener { mainViewModel.retry() }
    }

    private fun showContent() {
        main_recycler_view.isVisible = true
        error_layout.isGone = true
    }

    private fun showError() {
        main_recycler_view.isGone = true
        error_layout.isVisible = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel.initialLoaded.observe(viewLifecycleOwner, Observer { loaded ->
            if(loaded) showContent()
        })

        mainViewModel.pokemonList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        mainViewModel.initialLoading.observe(viewLifecycleOwner, Observer {
            refresh_layout.isRefreshing = it
        })

        mainViewModel.error.observe(viewLifecycleOwner, Observer { hasError ->
            if(hasError) showError() else showContent()
        })

        adapter.onItemClickListener = {_, _, pokemon ->
            pokemon?.let {
                findNavController().navigate(PokemonDetailFragmentDirections.actionPokemonDetails(it.name))
            }
        }
    }

}

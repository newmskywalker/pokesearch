package com.mateoj.pokesearch.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mateoj.pokesearch.R
import com.mateoj.pokesearch.ui.results.PokemonListAdapter
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*
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
        view.main_recycler_view.adapter = adapter
        view.main_recycler_view.layoutManager = LinearLayoutManager(context)
    }

    private fun showLoading() {
        main_recycler_view.isGone = true
        main_progressbar.isVisible = true
    }

    private fun showContent() {
        main_recycler_view.isVisible = true
        main_progressbar.isGone = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel.initialLoading.observe(viewLifecycleOwner, Observer {loading ->
            if(loading)showLoading()
        })

        mainViewModel.initialLoaded.observe(viewLifecycleOwner, Observer { loaded ->
            if(loaded) showContent()
        })

        mainViewModel.pokemonList.observe(viewLifecycleOwner, Observer {
            Log.d("POKEMON", it.toString())
            adapter.submitList(it)
        })
    }

}

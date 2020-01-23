package com.mateoj.pokesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.main_activity.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel : MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        setupSearch()
    }

    private fun hideSearch() {
        search_view.dismissSuggestions()
        search_view.closeSearch()
        search_view.hideKeyboard(search_view)
    }

    private fun showPokemonDetails(name: String?) {
        name?.let {
            findNavController(R.id.nav_host_fragment).navigate(MainNavDirections.actionPokemonDetails(it))
        } ?: run {
            Toast.makeText(this, "Pokemon Name null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearch() {
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showPokemonDetails(query)
                hideSearch()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mainViewModel.setTerm(newText)
                return true
            }
        })

        mainViewModel.suggestions.observe(this, Observer {
            search_view.setSuggestions(it)
            search_view.setOnItemClickListener { _, _, position, _ ->
                showPokemonDetails(it.getOrNull(position))
                hideSearch()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)

        val item = menu?.findItem(R.id.action_search)
        search_view.setMenuItem(item)
        return true
    }

    override fun onBackPressed() {
        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        } else {
            super.onBackPressed()
        }
    }
}

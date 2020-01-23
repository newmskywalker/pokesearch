package com.mateoj.pokesearch.ui.results

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mateoj.pokesearch.R
import com.mateoj.pokesearch.api.Pokemon
import com.mateoj.pokesearch.extensions.inflateChild
import com.mateoj.pokesearch.extensions.load

class PokemonListAdapter : PagedListAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder =
        PokemonViewHolder(parent.inflateChild(R.layout.item_pokemon_list))

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView = view.findViewById<TextView>(R.id.item_pokemon_name)
        private val imageView = view.findViewById<ImageView>(R.id.item_pokemon_image)

        fun bind(pokemon: Pokemon?) {
            pokemon?.let {
                nameTextView.text = it.name.capitalize()
                val image = it.sprites.frontShiny ?: it.sprites.frontDefault
                image?.let { imageView.load(it) }
            } ?: run {
                //This is a placeholder, clear
                clear()
            }
        }

        //Clears all the viewholder views
        private fun clear(){
            nameTextView.text = null
            imageView.setImageDrawable(null)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean  = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean  = oldItem == newItem
        }
    }
}
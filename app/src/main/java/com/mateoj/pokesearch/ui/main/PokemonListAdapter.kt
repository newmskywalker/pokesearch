package com.mateoj.pokesearch.ui.main

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

typealias OnPokemonClickListener = (View, Int, Pokemon?) -> Unit

class PokemonListAdapter : PagedListAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(
    DIFF_CALLBACK
) {
    var onItemClickListener: OnPokemonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder =
        PokemonViewHolder(parent.inflateChild(R.layout.item_pokemon_list)).apply {
            this.itemView.setOnClickListener {
                onItemClickListener?.invoke(it, this.adapterPosition, getItem(this.adapterPosition))
            }
        }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView = view.findViewById<TextView>(R.id.item_pokemon_name)
        private val imageView = view.findViewById<ImageView>(R.id.item_pokemon_image)
        private val pokemonIdView = view.findViewById<TextView>(R.id.item_pokemon_id)
        private val context = view.context

        fun bind(pokemon: Pokemon?) {
            pokemon?.let {
                nameTextView.text = it.name.capitalize()
                pokemonIdView.text = context.getString(R.string.pokemon_id, "${it.id}".padStart(3, '0'))
                imageView.load(it.sprites.frontDefault)
            } ?: run {
                //This is a placeholder, clear
                clear()
            }
        }

        //Clears all the viewholder views
        private fun clear(){
            nameTextView.text = null
            imageView.setImageDrawable(null)
            pokemonIdView.text = null
        }
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean  = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean  = oldItem == newItem
        }
    }
}
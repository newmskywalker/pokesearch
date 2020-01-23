package com.mateoj.pokesearch.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonSuggestion(@PrimaryKey val id: Int,
                             @ColumnInfo(name = "name") val name: String)
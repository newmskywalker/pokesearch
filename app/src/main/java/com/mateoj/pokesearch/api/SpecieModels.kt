package com.mateoj.pokesearch.api

import com.squareup.moshi.Json
/** Autogenerated **/

data class Specie (
    @Json(name = "base_happiness")
    val baseHappiness: Long,

    @Json(name = "capture_rate")
    val captureRate: Long,

    val color: NameUrl,

    @Json(name = "egg_groups")
    val eggGroups: List<NameUrl>,

    @Json(name = "evolution_chain")
    val evolutionChain: EvolutionChain,

    @Json(name = "evolves_from_species")
    val evolvesFromSpecies: NameUrl?,

    @Json(name = "flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,

    @Json(name = "form_descriptions")
    val formDescriptions: List<Any?>,

    @Json(name = "forms_switchable")
    val formsSwitchable: Boolean,

    @Json(name = "gender_rate")
    val genderRate: Long,

    val genera: List<Genus>,
    val generation: NameUrl,

    @Json(name = "growth_rate")
    val growthRate: NameUrl,

    val habitat: NameUrl,

    @Json(name = "has_gender_differences")
    val hasGenderDifferences: Boolean,

    @Json(name = "hatch_counter")
    val hatchCounter: Long,

    val id: Long,

    @Json(name = "is_baby")
    val isBaby: Boolean,

    val name: String,
    val names: List<Name>,
    val order: Long,

    @Json(name = "pal_park_encounters")
    val palParkEncounters: List<PalParkEncounter>,

    @Json(name = "pokedex_numbers")
    val pokedexNumbers: List<PokedexNumber>,

    val shape: NameUrl,
    val varieties: List<Variety>
)

data class NameUrl (
    val name: String,
    val url: String
)

data class EvolutionChain (
    val url: String
)

data class FlavorTextEntry (
    @Json(name = "flavor_text")
    val flavorText: String,

    val language: NameUrl,
    val version: NameUrl
)

data class Genus (
    val genus: String,
    val language: NameUrl
)

data class Name (
    val language: NameUrl,
    val name: String
)

data class PalParkEncounter (
    val area: NameUrl,

    @Json(name = "base_score")
    val baseScore: Long,

    val rate: Long
)

data class PokedexNumber (
    @Json(name = "entry_number")
    val entryNumber: Long,

    val pokedex: NameUrl
)

data class Variety (
    @Json(name = "is_default")
    val isDefault: Boolean,

    val pokemon: NameUrl
)
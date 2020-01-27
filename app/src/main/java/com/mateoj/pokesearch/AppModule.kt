package com.mateoj.pokesearch

import androidx.room.Room
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.db.PokemonSearchSuggestionsDB
import com.mateoj.pokesearch.repository.DefaultPokemonRepository
import com.mateoj.pokesearch.repository.PokemonListDataSourceFactory
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.ui.detail.PokemonDetailViewModel
import com.mateoj.pokesearch.ui.main.MainViewModel
import com.mateoj.pokesearch.ui.main.PokemonListAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
object AppModule {
    val appModule = module {
        single {
            Room.databaseBuilder(get(), PokemonSearchSuggestionsDB::class.java, "pokemon")
                .fallbackToDestructiveMigration().build()
        }
        single {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/")
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build())
                )
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build())
                .build()
                .create(PokeApiService::class.java)
        }
        factory { PokemonListDataSourceFactory(get(), get()) }
        single { DefaultPokemonRepository(get(), get()) as PokemonRepository }
        viewModel { MainViewModel(get()) }
        viewModel { MainActivityViewModel(get()) }
        viewModel { PokemonDetailViewModel(get()) }
        factory { PokemonListAdapter() }
    }
}

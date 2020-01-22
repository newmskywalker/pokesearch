package com.mateoj.pokesearch

import android.app.Application
import com.mateoj.pokesearch.repository.DefaultPokemonRepository
import com.mateoj.pokesearch.api.PokeApiService
import com.mateoj.pokesearch.repository.PokemonListDataSourceFactory
import com.mateoj.pokesearch.repository.PokemonRepository
import com.mateoj.pokesearch.ui.main.MainViewModel
import com.mateoj.pokesearch.ui.results.PokemonListAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PokeSearchApp : Application() {
    private val appModule = module {
        single {
            Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/")
                .addConverterFactory(MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build())
                )
                .client(OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build())
                .build()
                .create(PokeApiService::class.java)
        }

        factory { (scope: CoroutineScope) -> PokemonListDataSourceFactory(scope, get()) }

        single {
            (scope: CoroutineScope) -> DefaultPokemonRepository(scope) as PokemonRepository
        }
        viewModel {
            MainViewModel()
        }
        factory { PokemonListAdapter() }
    }
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PokeSearchApp)
            modules(appModule)
        }
    }
}
package com.mateoj.pokesearch

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PokeSearchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokeSearchApp)
            modules(AppModule.appModule)
        }
    }
}
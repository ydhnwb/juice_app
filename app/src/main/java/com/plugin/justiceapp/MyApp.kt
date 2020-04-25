package com.plugin.justiceapp

import android.app.Application
import com.plugin.justiceapp.viewmodels.BranchViewModel
import com.plugin.justiceapp.viewmodels.ProductViewModel
import com.plugin.justiceapp.webservices.JustApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApp)
            modules(listOf(retrofitModules, viewModelModules))
        }
    }
}

val retrofitModules = module {
    single { JustApi.instance() }
}

val viewModelModules = module {
    viewModel { ProductViewModel(get()) }
    viewModel { BranchViewModel(get()) }
}
package com.mibaldi.fitapp

import android.app.Application
import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.fitapp.appData.server.FitAppDbDataSource
import com.mibaldi.fitapp.ui.main.MainActivity
import com.mibaldi.fitapp.ui.main.MainViewModel
import com.mibaldi.usecases.GetTrainings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI(){
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(appModule, dataModule, scopesModule))
    }
}

private val appModule = module {
    single(named("apiKey")) { androidApplication().getString(R.string.api_key) }
    factory<RemoteDataSource> { FitAppDbDataSource(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
    single(named("baseUrl")) { "https://api.themoviedb.org/3/" }
}

val dataModule = module {
    factory { TrainingsRepository(get()) }
}

private val scopesModule = module {
    scope(named<MainActivity>()) {
        viewModel { MainViewModel(get(), get()) }
        scoped { GetTrainings(get()) }
    }
}
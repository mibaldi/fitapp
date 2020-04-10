package com.mibaldi.fitapp

import android.app.Application
import com.mibaldi.data.repository.PermissionChecker
import com.mibaldi.data.repository.RegionRepository
import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.data.source.LocationDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.fitapp.appData.AndroidPermissionChecker
import com.mibaldi.fitapp.appData.PlayServicesLocationDataSource
import com.mibaldi.fitapp.appData.server.FitAppDb
import com.mibaldi.fitapp.appData.server.FitAppDbDataSource
import com.mibaldi.fitapp.appData.servermock.FitAppDbDataSourceMock
import com.mibaldi.fitapp.services.Analytics
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.services.FirebaseAnalytics
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.base.BaseViewModel
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.detail.DetailActivity
import com.mibaldi.fitapp.ui.detail.DetailViewModel
import com.mibaldi.fitapp.ui.main.MainActivity
import com.mibaldi.fitapp.ui.main.MainViewModel
import com.mibaldi.usecases.FindTrainingById
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
    single<Analytics> { FirebaseAnalytics() }
    single {  AnalyticsCallbacks(get()) }
    single {  DialogManager() }
    //factory<RemoteDataSource> { FitAppDbDataSource(get()) }
    factory<RemoteDataSource> { FitAppDbDataSourceMock() }
    factory<LocationDataSource>{PlayServicesLocationDataSource(get()) }
    factory<PermissionChecker> {AndroidPermissionChecker(get())}
    single<CoroutineDispatcher> { Dispatchers.Main }
    single(named("baseUrl")) { "https://api.themoviedb.org/3/" }
    single {FitAppDb(get(named("baseUrl")))}
    factory { FindTrainingById(get()) }
    viewModel { BaseViewModel(get(),get()) }
}

val dataModule = module {
    factory { RegionRepository(get(),get()) }
    factory { TrainingsRepository(get(),get()) }
}

private val scopesModule = module {
    scope(named<MainActivity>()) {
        viewModel { MainViewModel(get(), get()) }
        scoped { GetTrainings(get()) }
    }

    scope(named<DetailActivity>()) {
        viewModel { (id: Int) -> DetailViewModel(id, get(),get()) }
        scoped { FindTrainingById(get()) }
    }
}


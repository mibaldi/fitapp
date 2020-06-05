package com.mibaldi.fitapp.app

import android.app.Application
import com.mibaldi.data.repository.AuthRepository
import com.mibaldi.data.repository.PermissionChecker
import com.mibaldi.data.repository.RegionRepository
import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.data.source.AuthRemoteDataSource
import com.mibaldi.data.source.LocationDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.appData.AndroidPermissionChecker
import com.mibaldi.fitapp.appData.PlayServicesLocationDataSource
import com.mibaldi.fitapp.appData.server.AuthDbDataSource
import com.mibaldi.fitapp.appData.server.FitAppDb
import com.mibaldi.fitapp.appData.server.FitAppDbDataSource
import com.mibaldi.fitapp.appData.servermock.FileLocalDb
import com.mibaldi.fitapp.services.Analytics
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.services.FirebaseAnalytics
import com.mibaldi.fitapp.ui.admin.AdminActivity
import com.mibaldi.fitapp.ui.auth.FirebaseUIActivity
import com.mibaldi.fitapp.ui.auth.FirebaseViewModel
import com.mibaldi.fitapp.ui.base.BaseViewModel
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.detail.DetailActivity
import com.mibaldi.fitapp.ui.detail.DetailViewModel
import com.mibaldi.fitapp.ui.main.MainActivity
import com.mibaldi.fitapp.ui.main.MainViewModel
import com.mibaldi.fitapp.ui.place.PlaceActivity
import com.mibaldi.fitapp.ui.place.PlaceViewModel
import com.mibaldi.fitapp.ui.profile.ProfileActivity
import com.mibaldi.fitapp.ui.profile.ProfileViewModel
import com.mibaldi.fitapp.ui.training.TrainingActivity
import com.mibaldi.fitapp.ui.training.TrainingViewModel
import com.mibaldi.fitapp.ui.workoutTimer.WorkoutTimerActivity
import com.mibaldi.fitapp.ui.workoutTimer.WorkoutTimerViewModel
import com.mibaldi.usecases.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI(){
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(
            appModule,
            dataModule,
            scopesModule,
            datasourcesModule,
            usecasesModule
        ))
    }
}

val appModule = module {
    single(named("apiKey")) { androidApplication().getString(R.string.api_key) }
    single {  AnalyticsCallbacks(get()) }
    single {  DialogManager() }
    viewModel { BaseViewModel(get()) }
    single { FileLocalDb(get()) }

}
val usecasesModule = module {
    factory { FindTrainingById(get()) }
    factory { ImportTrainings(get()) }
}
val datasourcesModule = module {
    single<Analytics> { FirebaseAnalytics() }

    single<CoroutineDispatcher> { Dispatchers.Main }
    single {FitAppDb(get(named("baseUrl")))}
    single(named("baseUrl")) { "https://api.themoviedb.org/3/" }
    //factory<RemoteDataSource> { FitAppDbDataSource(get()) }
    factory<RemoteDataSource> { FitAppDbDataSource(get()) }
    factory<LocationDataSource>{PlayServicesLocationDataSource(get()) }
    factory<PermissionChecker> {AndroidPermissionChecker(get())}
    factory<AuthRemoteDataSource> {AuthDbDataSource()}
}

val dataModule = module {
    factory { RegionRepository(get(),get()) }
    factory { TrainingsRepository(get(),get()) }
    factory { AuthRepository(get())}
}

private val scopesModule = module {
    scope(named<MainActivity>()) {
        viewModel { MainViewModel(get(),get()) }
        scoped { GetTrainings(get()) }
    }

    scope(named<DetailActivity>()) {
        viewModel { (id: String) -> DetailViewModel(id, get()) }
        scoped { FindTrainingById(get()) }
    }

    scope(named<PlaceActivity>()) {
        viewModel { PlaceViewModel() }
    }
    scope(named<ProfileActivity>()) {
        viewModel { ProfileViewModel(get(),get(),get()) }
        scoped { SendWeight(get()) }
        scoped { GetWeights(get()) }

    }
    scope(named<TrainingActivity>()) {
        viewModel { TrainingViewModel(get()) }
        scoped { GetTrainingsHashMap(get()) }
    }
    scope(named<WorkoutTimerActivity>()) {
        viewModel { WorkoutTimerViewModel() }
    }
    scope(named<FirebaseUIActivity>()) {
        viewModel { FirebaseViewModel(get()) }
        scoped { RegisterUser(get()) }
    }
    scope(named<AdminActivity>()) {
        viewModel { com.mibaldi.fitapp.ui.admin.AdminViewModel(get(),get()) }
        scoped { GetUsers(get()) }
    }
}


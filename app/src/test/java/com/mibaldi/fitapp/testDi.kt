package com.mibaldi.fitapp

import android.app.Activity
import com.mibaldi.data.repository.PermissionChecker
import com.mibaldi.data.source.LocationDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.fitapp.app.appModule
import com.mibaldi.fitapp.app.dataModule
import com.mibaldi.fitapp.app.usecasesModule
import com.mibaldi.fitapp.services.Analytics
import com.mibaldi.fitapp.services.FirebaseAnalytics
import com.mibaldi.testshared.mockedTraining
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initMockedDi(vararg modules: Module) {
    startKoin {
        modules(listOf(mockedDataSourceModule, usecasesModule,dataModule,appModule) + modules)
    }
}

private val mockedDataSourceModule = module {
    single<RemoteDataSource> { FakeRemoteDataSource() }
    single<LocationDataSource> { FakeLocationDataSource() }
    single<PermissionChecker> { FakePermissionChecker() }
    single<Analytics> { FakeFirebaseAnalytics() }

    single { Dispatchers.Unconfined }
}

val defaultFakeTrainings = listOf(
    mockedTraining.copy(1),
    mockedTraining.copy(2),
    mockedTraining.copy(3),
    mockedTraining.copy(4)
)


class FakeRemoteDataSource : RemoteDataSource {

    var trainings = defaultFakeTrainings

    override suspend fun getTrainings() = Either.Right(trainings)
    override suspend fun findById(id: Int) = Either.Right(mockedTraining.copy(id))
}

class FakeLocationDataSource : LocationDataSource {
    var location = "US"

    override suspend fun findLastRegion(): String? = location
}

class FakePermissionChecker : PermissionChecker {
    var permissionGranted = true

    override suspend fun check(permission: PermissionChecker.Permission): Boolean =
        permissionGranted
}

class FakeFirebaseAnalytics : Analytics {
    override fun activityCreated(activity: Activity) {

    }

    override fun activityResumed(activity: Activity) {
    }

    override fun activityPaused(activity: Activity) {
    }

    override fun activityDestroyed(activity: Activity) {
    }

    override fun logEvent(event: String) {
    }

    override fun logError(error: String) {
    }

}
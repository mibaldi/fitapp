package com.mibaldi.fitapp

import com.mibaldi.data.repository.PermissionChecker
import com.mibaldi.data.source.LocationDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.fitapp.app.dataModule
import com.mibaldi.testshared.mockedTraining
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initMockedDi(vararg modules: Module) {
    startKoin {
        modules(listOf(mockedAppModule, dataModule) + modules)
    }
}

private val mockedAppModule = module {
    single(named("apiKey")) { "12456" }
    single<RemoteDataSource> { FakeRemoteDataSource() }
    single<LocationDataSource> { FakeLocationDataSource() }
    single<PermissionChecker> { FakePermissionChecker() }

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
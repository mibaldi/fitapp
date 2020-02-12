package com.mibaldi.data.repository

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.testshared.mockedTraining
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TrainingsRepositoryTest {

    @Mock
    lateinit var remoteDataSource: RemoteDataSource
    @Mock
    lateinit var regionRepository: RegionRepository


    lateinit var trainingsRepository: TrainingsRepository

    @Before
    fun setUp() {
        trainingsRepository =
            TrainingsRepository(remoteDataSource,regionRepository)
    }

    @Test
    fun `getTrainings gets from remote data source first`() {
        runBlocking {

            val remoteTrainings = listOf(mockedTraining.copy(1))
            whenever(remoteDataSource.getTrainings()).thenReturn(Either.Right(remoteTrainings))

            val result = trainingsRepository.getTrainings()

            assertEquals(remoteTrainings, result)
        }
    }
}
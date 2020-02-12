package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.testshared.mockedTraining
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetTrainingTest {

    @Mock
    lateinit var trainingsRepository: TrainingsRepository

    lateinit var getTrainings: GetTrainings

    @Before
    fun setUp() {
        getTrainings = GetTrainings(trainingsRepository)
    }

    @Test
    fun `invoke calls trainings repository`() {
        runBlocking {

            val trainings = listOf(mockedTraining.copy(id = 1))
            whenever(trainingsRepository.getTrainings()).thenReturn(trainings)

            val result = getTrainings.invoke()

            Assert.assertEquals(trainings, result)
        }
    }
}
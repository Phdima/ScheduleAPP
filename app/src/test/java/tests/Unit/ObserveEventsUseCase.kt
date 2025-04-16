package tests.Unit

import app.cash.turbine.test
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import com.example.scheduleapp.domain.useCases.ObserveEventsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test


class ObserveEventsUseCase {


    @Test
    fun `invoke() should call observeUpcomingEvents from repository`() = runTest {

        val mockRepo = mockk<ScheduleRepository>()
        val useCase = ObserveEventsUseCase(mockRepo)

        coEvery { mockRepo.observeUpcomingEvents() } returns emptyFlow()


        useCase().toList()


        coVerify(exactly = 1) { mockRepo.observeUpcomingEvents() }
    }

    @Test
    fun `invoke() should emit data from repository`() = runTest {

        val mockRepo = mockk<ScheduleRepository>()
        val useCase = ObserveEventsUseCase(mockRepo)
        val testData = listOf(
            ScheduleEvent(
                id = 1,
                startTime = Instant.parse("2024-01-01T10:00:00Z"),
                title = "Test",
                description = "Test",
            ),
            ScheduleEvent(
                id = 2,
                startTime = Instant.parse("2024-01-01T12:00:00Z"),
                title = "Test",
                description = "Test",
            ),
        )
        coEvery { mockRepo.observeUpcomingEvents() } returns flowOf(testData)


        useCase().test {
            assertEquals(testData, awaitItem())
            awaitComplete()
        }
    }
}
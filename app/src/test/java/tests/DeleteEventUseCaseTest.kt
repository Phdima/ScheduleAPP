package tests

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import com.example.scheduleapp.domain.useCases.DeleteEventUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test

class DeleteEventUseCaseTest {
    private val repo = mockk<ScheduleRepository>()
    private lateinit var useCase: DeleteEventUseCase

    @Before
    fun setup() {
        clearAllMocks()
        useCase = DeleteEventUseCase(repo)
    }

    @Test
    fun `should delete event from repo`() = runTest {

        val event = ScheduleEvent(
            id = 1,
            startTime = Instant.parse("2024-01-01T10:00:00Z"),
            title = "Test",
            description = "Test",
        )

        coEvery { repo.deleteEvent(event) } returns Unit

        useCase(event)

        coVerify(exactly = 1) { repo.deleteEvent(event) }
    }
}
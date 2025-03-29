package tests

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import com.example.scheduleapp.domain.useCases.AddEventUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import org.junit.Test
import kotlin.test.assertFailsWith
import io.mockk.mockk

class AddEventUseCaseTest {
    @Test
    fun `adding past event should throw exception`() = runTest {

        val repo = mockk<ScheduleRepository>(relaxed = true)

        val useCase = AddEventUseCase(repo)

        val pastEvent = ScheduleEvent(
            title = "Test",
            description = "Test",
            startTime = Clock.System.now() - 1.days
        )

        assertFailsWith<IllegalArgumentException> {
            useCase(pastEvent)
        }
    }
}
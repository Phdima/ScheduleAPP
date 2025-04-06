package tests

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import com.example.scheduleapp.domain.useCases.AddEventUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import org.junit.Test
import kotlin.test.assertFailsWith
import io.mockk.mockk
import org.junit.Before
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class AddEventUseCaseTest {

    private val repo = mockk<ScheduleRepository>()
    private lateinit var useCase: AddEventUseCase

    @Before
    fun setup(){
        clearAllMocks()
        useCase = AddEventUseCase(repo)
    }

    @Test
    fun `adding past event should throw exception`() = runTest {

        val pastEvent = ScheduleEvent(
            title = "Test",
            description = "Test",
            startTime = Clock.System.now() - 1.minutes
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.invoke(pastEvent)
        }

    }

    @Test
    fun `adding future event should pass`() = runTest {

        val futureEvent = ScheduleEvent(
            title = "Test",
            description = "Test",
            startTime = Clock.System.now() + 15.minutes
        )
        coEvery { repo.addEvent(futureEvent) } returns Unit


        useCase.invoke(futureEvent)


        coVerify(exactly = 1) { repo.addEvent(futureEvent) }
    }
}


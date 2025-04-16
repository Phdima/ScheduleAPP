package tests.Unit

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import app.cash.turbine.test
import com.example.scheduleapp.data.notifWork.ClockProvider
import com.example.scheduleapp.data.notifWork.ScheduleRepositoryImpl
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.google.android.datatransport.runtime.time.TestClock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours


@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleRepositoryTest {
    private val mockDao = mockk<ScheduleDao>(relaxed = true)
    private val mockWorkManager = mockk<WorkManager>()
    private val mockClock = mockk<ClockProvider>()
    private val testDispatcher = StandardTestDispatcher()

    private var repository = ScheduleRepositoryImpl(mockDao, mockWorkManager, mockClock)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockClock.currentTime() } returns Instant.parse("2023-09-20T10:00:00Z")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `scheduleNotification should use correct UTC time`() = runTest {

        val moscowTimeZone = TimeZone.of("Europe/Moscow")
        val localDateTime = LocalDateTime(2023, 9, 20, 15, 0)
        val eventStartTime = localDateTime.toInstant(moscowTimeZone)

        val event = ScheduleEvent(
            id = 1,
            startTime = eventStartTime,
            notificationOffset = 1.hours,
            title = "Test",
            description = "Test"
        )

        val requestSlot = slot<OneTimeWorkRequest>()
        every {
            mockWorkManager.enqueueUniqueWork(
                eq("event_1"),
                any(),
                capture(requestSlot)
            )
        } returns mockk()


        repository.scheduleNotification(event)


        val expectedDelay = 3600000L
        assertEquals(expectedDelay, requestSlot.captured.workSpec.initialDelay)
    }

    @Test
    fun `addEvent should save to DAO and schedule notification`() = runTest {

        val initialTime = Instant.parse("2024-01-01T10:00:00Z").toEpochMilliseconds()
        val testClock = TestClock(initialTime)


        val eventId = 123L
        val eventStartTime =
            Instant.fromEpochMilliseconds(initialTime + 2.hours.inWholeMilliseconds)
        val notificationOffset = 1.hours

        val event = ScheduleEvent(
            title = "Meeting",
            description = "Team sync",
            startTime = eventStartTime,
            notificationOffset = notificationOffset
        )


        coEvery { mockDao.insert(any()) } returns eventId
        val workRequestSlot = slot<OneTimeWorkRequest>()
        every {
            mockWorkManager.enqueueUniqueWork(
                eq("event_$eventId"),
                eq(ExistingWorkPolicy.REPLACE),
                capture(workRequestSlot)
            )
        } returns mockk()


        repository = ScheduleRepositoryImpl(
            mockDao,
            mockWorkManager,
            object : ClockProvider {
                override fun currentTime() = Instant.fromEpochMilliseconds(testClock.time)
            }
        )


        repository.addEvent(event)


        coVerify {
            mockDao.insert(match { entity ->
                entity.startTime == eventStartTime
            })
        }


        val expectedDelay = 1.hours.inWholeMilliseconds


        assertEquals(
            expectedDelay,
            workRequestSlot.captured.workSpec.initialDelay,
            "Notification delay is incorrect"
        )
    }


    @Test
    fun `observeUpcomingEvents should return sorted events`() = runTest {

        val now = Instant.parse("2024-01-01T00:00:00Z")
        val entities = listOf(
            ScheduleEventEntity(
                id = 1,
                title = "Event B",
                description = "",
                startTime = now.plus(2.hours),
                notificationOffset = 3600000L,
                notificationTime = now.plus(1.hours).toEpochMilliseconds(),
                color = 0xFF0000
            ),
            ScheduleEventEntity(
                id = 2,
                title = "Event A",
                description = "",
                startTime = now.plus(1.hours),
                notificationOffset = 3600000L,
                notificationTime = now.toEpochMilliseconds(),
                color = 0x00FF00
            )
        )

        coEvery {
            mockDao.getEventsBetween(any<Long>())
        } returns flowOf(entities)


        repository.observeUpcomingEvents().test {

            val items = awaitItem()

            assertEquals(2, items.size)
            assertEquals("Event A", items[0].title)
            assertEquals("Event B", items[1].title)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getEventsForNotification should return events in time range`() = runTest {

        val start = Instant.parse("2024-01-01T09:00:00Z")
        val end = Instant.parse("2024-01-01T11:00:00Z")

        val entity = ScheduleEventEntity(
            id = 1,
            title = "Morning Meeting",
            description = "",
            startTime = Instant.parse("2024-01-01T10:30:00Z"),
            notificationOffset = 3600000L,
            notificationTime = Instant.parse("2024-01-01T10:00:00Z").toEpochMilliseconds(),
            color = 0xFF0000
        )

        coEvery {
            mockDao.getEventsForNotification(
                start = start.toEpochMilliseconds(),
                end = end.toEpochMilliseconds()
            )
        } returns listOf(entity)


        val result = repository.getEventsForNotification(start..end)


        assertEquals(1, result.size)
        assertEquals("Morning Meeting", result[0].title)
        assertEquals(Instant.parse("2024-01-01T10:30:00Z"), result[0].startTime)
    }

    @Test
    fun `ui time picker should convert to correct instant`() {

        val dateTimePicker = "2024-01-01T15:00"
        val timeZone = TimeZone.of("Europe/Moscow")


        val parsedTime = LocalDateTime.parse(dateTimePicker)
        val instant = parsedTime.toInstant(timeZone)


        assertEquals("2024-01-01T12:00:00Z", instant.toString())
    }

    @Test
    fun `time zone conversion should be correct`() {
        // Arrange
        val localTime = LocalDateTime(2024, 1, 1, 15, 0) // MSK (UTC+3)
        val moscowTimeZone = TimeZone.of("Europe/Moscow")

        // Act
        val utcInstant = localTime.toInstant(moscowTimeZone)

        // Assert
        assertEquals("2024-01-01T12:00:00Z", utcInstant.toString())
    }

}

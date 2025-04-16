package Integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.scheduleapp.data.notifWork.ScheduleRepositoryImpl
import com.example.scheduleapp.data.room.dataBase.AppDataBase
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.hours

@RunWith(AndroidJUnit4::class)
@MediumTest
class IntegrationTest {
    private lateinit var db: AppDataBase
    private lateinit var workManager: WorkManager
    private lateinit var repository: ScheduleRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()


        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)



        mockkObject(Clock.System)
        val fixedTime = Instant.parse("2024-01-01T00:00:00Z")
        every { Clock.System.now() } returns fixedTime


        db = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
            .allowMainThreadQueries()
            .build()




        repository = ScheduleRepositoryImpl(db.scheduleDao(), workManager)
    }

    @Test
    fun eventShouldBeSavedAndNotificationScheduled() = runTest {

        val event = ScheduleEvent(
            title = "Тестовая встреча",
            description = "smth",
            startTime = Clock.System.now().plus(2.hours),
            notificationOffset = 1.hours
        )


        repository.addEvent(event)
        testScheduler.advanceUntilIdle()


        val savedEvents = repository.observeUpcomingEvents().first()
        assertEquals(1, savedEvents.size)
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
        if (::workManager.isInitialized) workManager.cancelAllWork()
        unmockkAll()
    }
}
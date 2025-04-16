package com.example.scheduleapp.data.notifWork

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

import com.example.scheduleapp.data.mapping.toDomain
import com.example.scheduleapp.data.mapping.toEntity

import com.example.scheduleapp.data.mapping.toLocalDateTime
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration

class ScheduleRepositoryImpl @Inject constructor(
    private val dao: ScheduleDao,
    private val workManager: WorkManager,
    private val clockProvider: ClockProvider = SystemClockProvider()
) : ScheduleRepository {

    override suspend fun addEvent(event: ScheduleEvent) {
        val entity = event.toEntity()
        val id = dao.insert(entity)
        scheduleNotification(event.copy(id = id))
    }

    override suspend fun deleteEvent(event: ScheduleEvent) {
        val entity = event.toEntity()
        dao.delete(entity)
        workManager.cancelUniqueWork("event_${entity.id}")
    }

    override fun observeUpcomingEvents(): Flow<List<ScheduleEvent>> {

        return dao.getEventsBetween(Clock.System.now().toEpochMilliseconds())
            .map { entities ->
                entities.map { it.toDomain() }.sortedBy { it.startTime }
            }
    }

    override suspend fun getEventsForNotification(timeRange: ClosedRange<Instant>): List<ScheduleEvent> {
        return dao.getEventsForNotification(
            start = timeRange.start.toEpochMilliseconds(),
            end = timeRange.endInclusive.toEpochMilliseconds()
        ).map { it.toDomain() }
    }


    internal fun scheduleNotification(event: ScheduleEvent) {

        val utcNotificationTime = event.startTime
            .minus(event.notificationOffset)


        val delay = utcNotificationTime - clockProvider.currentTime()

        val positiveDelay = maxOf(delay, Duration.ZERO)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(positiveDelay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("event_id" to event.id))
            .build()

        workManager.enqueueUniqueWork(
            "event_${event.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}

interface ClockProvider {
    fun currentTime(): Instant
}


class SystemClockProvider : ClockProvider {
    override fun currentTime() = Clock.System.now()
}
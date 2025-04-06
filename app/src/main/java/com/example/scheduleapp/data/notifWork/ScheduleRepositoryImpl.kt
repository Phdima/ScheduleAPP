package com.example.scheduleapp.data.notifWork

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.scheduleapp.data.mapping.toDomain
import com.example.scheduleapp.data.mapping.toEntity
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class ScheduleRepositoryImpl @Inject constructor(
    private val dao: ScheduleDao,
    private val workManager: WorkManager
) : ScheduleRepository {
    override suspend fun addEvent(event: ScheduleEvent) {
        val entity = event.toEntity()
        val id = dao.insert(entity)
        scheduleNotification(event.copy(id = id))
    }

    override suspend fun deleteEvent(eventId: Long) {
        TODO("Not yet implemented")
    }

    override fun observeUpcomingEvents(): Flow<List<ScheduleEvent>> {

        return dao.getEventsBetween(Clock.System.now(), Clock.System.now() + 7.days)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun getEventsForNotification(timeRange: ClosedRange<Instant>): List<ScheduleEvent> {
        TODO("Not yet implemented")
    }

    private fun scheduleNotification(event: ScheduleEvent) {
        val delay = event.startTime - Clock.System.now() - event.notificationOffset

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("event_id" to event.id))
            .build()

        workManager.enqueueUniqueWork(
            "event_${event.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
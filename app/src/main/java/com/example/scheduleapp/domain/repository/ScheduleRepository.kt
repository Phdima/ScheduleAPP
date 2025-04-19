package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.ScheduleEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface ScheduleRepository {
    suspend fun addEvent(event: ScheduleEvent)
    suspend fun deleteEvent(event: ScheduleEvent)
    fun observeUpcomingEvents(): Flow<List<ScheduleEvent>>

    suspend fun markNotificationShow(event: ScheduleEvent)
    suspend fun getEventsForNotification(timeRange: ClosedRange<Instant>): List<ScheduleEvent>
}
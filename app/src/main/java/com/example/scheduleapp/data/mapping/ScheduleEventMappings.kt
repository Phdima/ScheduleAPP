package com.example.scheduleapp.data.mapping

import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun ScheduleEvent.toEntity(): ScheduleEventEntity {
    return ScheduleEventEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        notificationOffset = this.notificationOffset.inWholeMilliseconds,
        color = this.color
    )
}

fun ScheduleEventEntity.toDomain(): ScheduleEvent{
    return ScheduleEvent(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        notificationOffset = notificationOffset.milliseconds,
        color = this.color
    )
}
package com.example.scheduleapp.data.mapping

import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent

fun ScheduleEvent.toEntity(): ScheduleEventEntity {
    return ScheduleEventEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        notificationOffset = this.notificationOffset,
        color = this.color
    )
}

fun ScheduleEventEntity.toDomain(): ScheduleEvent{
    return ScheduleEvent(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        notificationOffset = this.notificationOffset,
        color = this.color
    )
}
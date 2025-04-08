package com.example.scheduleapp.data.mapping

import android.annotation.SuppressLint
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
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

fun ScheduleEventEntity.toDomain(): ScheduleEvent {
    return ScheduleEvent(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        notificationOffset = notificationOffset.milliseconds,
        color = this.color
    )
}

fun Instant.format(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.dayOfMonth} " +
            "${localDateTime.month.name.take(3)} " +
            "${localDateTime.year}, " +
            "${localDateTime.hour.toString().padStart(2, '0')}:" +
            "${localDateTime.minute.toString().padStart(2, '0')}"
}

fun LocalDateTime.format(): String {
    val monthAbbreviation = month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return buildString {
        append(dayOfMonth.toString().padStart(2, '0'))
        append(" $monthAbbreviation ")
        append(year)
        append(", ")
        append(hour.toString().padStart(2, '0'))
        append(":")
        append(minute.toString().padStart(2, '0'))
    }
}
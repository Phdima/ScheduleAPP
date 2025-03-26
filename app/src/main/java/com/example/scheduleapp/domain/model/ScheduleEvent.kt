package com.example.scheduleapp.domain.model

import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class ScheduleEvent(
    val id: Long = 0,
    val title: String,
    val description: String,
    val startTime: Instant,
    val notificationOffset: Duration = 1.hours,
    val color: Long = 0xFF6750A4
)
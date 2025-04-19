package com.example.scheduleapp.domain.model

import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class ScheduleEvent(
    val id: Long = 0,
    val title: String,
    val description: String,
    val startTime: Instant,
    val notificationOffset: Duration = 1.hours,
    var isNotificated: Boolean = false,
    val color: Long = generateRandomColor()
)


fun generateRandomColor(): Long {
    val rgb = Random.nextInt(0x1000000)
    return 0xFF000000L or rgb.toLong()
}
package com.example.scheduleapp.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Entity(tableName = "events")
data class ScheduleEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    @ColumnInfo(name = "start_time") val startTime: Instant,
    @ColumnInfo(name = "notification_offset") val notificationOffset: Duration,
    val color: Long
)
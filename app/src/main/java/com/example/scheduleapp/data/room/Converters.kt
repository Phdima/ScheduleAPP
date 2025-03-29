package com.example.scheduleapp.data.room

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Converters {

    @TypeConverter
    fun instantFromEpochMilli(value: Long?): Instant? =
        value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun instantToEpochMilli(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()


    @TypeConverter
    fun durationFromMillis(value: Long?): Duration? =
        value?.milliseconds

    @TypeConverter
    fun durationToMillis(duration: Duration?): Long? =
        duration?.inWholeMilliseconds
}
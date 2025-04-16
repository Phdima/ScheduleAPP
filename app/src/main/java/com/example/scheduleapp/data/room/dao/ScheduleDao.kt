package com.example.scheduleapp.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(event: ScheduleEventEntity) : Long

    @Delete
    suspend fun delete(event: ScheduleEventEntity)

    @Query("SELECT * FROM events WHERE notification_time BETWEEN :start AND :end")
    suspend fun getEventsForNotification(start: Long, end: Long): List<ScheduleEventEntity>

    @Query("SELECT * FROM events WHERE start_time >= :start")
    fun getEventsBetween(start: Long): Flow<List<ScheduleEventEntity>>
}
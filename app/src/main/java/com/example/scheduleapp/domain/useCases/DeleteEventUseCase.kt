package com.example.scheduleapp.domain.useCases

import com.example.scheduleapp.data.mapping.format
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        repository.deleteEvent(event)
    }
}
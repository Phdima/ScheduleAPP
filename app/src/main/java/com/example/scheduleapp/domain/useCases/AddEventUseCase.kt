package com.example.scheduleapp.domain.useCases

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.datetime.Clock

class AddEventUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        require(event.startTime > Clock.System.now()){
            "Can't schedule event in the past"
        }
        repository.addEvent(event)
    }
}
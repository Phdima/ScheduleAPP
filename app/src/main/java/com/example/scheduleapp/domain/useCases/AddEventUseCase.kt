package com.example.scheduleapp.domain.useCases

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        require(event.startTime > Clock.System.now()){
            "Can't schedule event in the past"
        }
        repository.addEvent(event)
    }
}
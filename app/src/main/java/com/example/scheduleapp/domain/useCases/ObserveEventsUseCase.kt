package com.example.scheduleapp.domain.useCases

import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEventsUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke() : Flow<List<ScheduleEvent>>{
       return repository.observeUpcomingEvents()
    }
}
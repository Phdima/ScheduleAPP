package com.example.scheduleapp.presentation.state

import com.example.scheduleapp.domain.model.ScheduleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class EventStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(CreateEventState())
    val state: StateFlow<CreateEventState> = _state.asStateFlow()

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun updateStartTime(time: LocalDateTime) {
        _state.value = _state.value.copy(startTime = time)
    }
    fun createEventFromState(): ScheduleEvent {
        val currentState = state.value
        return ScheduleEvent(
            title = currentState.title,
            description = currentState.description,
            startTime = currentState.startTime.toInstant(TimeZone.currentSystemDefault()),
        )
    }
}

data class CreateEventState(
    val title: String = "",
    val description: String = "",
    val startTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    val notificationOffset: Duration = 1.hours,
    val selectedColor: Long = 0xFF6750A4,
)
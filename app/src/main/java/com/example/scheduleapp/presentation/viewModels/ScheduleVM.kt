package com.example.scheduleapp.presentation.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.useCases.AddEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class ScheduleVM @Inject constructor(
    private val addEventUseCase: AddEventUseCase
) : ViewModel() {

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

    private fun createEventFromState(): ScheduleEvent {

        return ScheduleEvent(
            title = _state.value.title,
            description = _state.value.description,
            startTime = _state.value.startTime.toInstant(TimeZone.UTC),
        )
    }


    fun addEvent() {
        viewModelScope.launch {
            addEventUseCase(createEventFromState())
        }
    }
}

data class CreateEventState(
    val title: String = "",
    val description: String = "",
    val startTime: LocalDateTime  = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val notificationOffset: Duration = 1.hours,
    val selectedColor: Long = 0xFF6750A4,
)
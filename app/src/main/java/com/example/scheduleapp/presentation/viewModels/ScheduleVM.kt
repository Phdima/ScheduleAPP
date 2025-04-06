package com.example.scheduleapp.presentation.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.useCases.AddEventUseCase
import com.example.scheduleapp.domain.useCases.ObserveEventsUseCase
import com.example.scheduleapp.presentation.state.EventStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    private val addEventUseCase: AddEventUseCase,
    private val observeEventsUseCase: ObserveEventsUseCase
) : ViewModel() {

    fun addEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            addEventUseCase(event)
        }
    }

    val events: Flow<List<ScheduleEvent>> = observeEventsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}


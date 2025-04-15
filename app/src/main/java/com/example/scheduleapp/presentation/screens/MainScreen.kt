@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scheduleapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scheduleapp.presentation.state.EventStateHolder
import com.example.scheduleapp.presentation.viewModels.ScheduleVM
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun MainScreen() {
    val viewModel: ScheduleVM = hiltViewModel()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = emptyList())
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val stateHolder = EventStateHolder()
    val state by stateHolder.state.collectAsStateWithLifecycle()

    var showScheduleCard by remember { mutableStateOf(false) }


    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ближайшие события") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showScheduleCard = !showScheduleCard
            }) {
                Icon(Icons.Default.Add, "Добавить")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            if (showScheduleCard) {
                ScheduleCardCreateView(
                    state = state,
                    onTitleChange = stateHolder::updateTitle,
                    onDescriptionChange = stateHolder::updateDescription,
                    onTimeChange = stateHolder::updateStartTime,
                    onAddEvent = {
                        val event = stateHolder.createEventFromState()
                        viewModel.addEvent(event)
                    },
                    onDismiss = { showScheduleCard = !showScheduleCard },
                    modifier = Modifier.padding()
                )
            } else {
                LazyColumn {
                    item {
                        LazyRow() {
                            items(events) { event ->
                                EventItem(
                                    event = event,
                                    onClick = { /* Обработка клика */ },
                                    onDelete = { viewModel.deleteEvent(event) }
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = "График на месяц ",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 10.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    item {
                        CalendarView()
                    }

                }
            }
        }
    }
}

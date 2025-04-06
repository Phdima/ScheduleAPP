@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scheduleapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduleapp.LocalNavController
import com.example.scheduleapp.presentation.state.EventStateHolder
import com.example.scheduleapp.presentation.viewModels.ScheduleVM


@Composable
fun MainScreen() {
    val viewModel: ScheduleVM = hiltViewModel()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = emptyList())
    val stateHolder = EventStateHolder()
    val state by stateHolder.state.collectAsStateWithLifecycle()
    var showScheduleCard by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Главный экран") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showScheduleCard = !showScheduleCard
            }) {
                Icon(Icons.Default.Add, "Добавить")
            }
        },
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            if (showScheduleCard) {
                ScheduleCard(
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
                   items(events) {
                       event ->
                       Box( modifier = Modifier.padding(paddingValue)){
                           Text(text = event.title)
                       }
                   }
                }
            }
        }
    }
}
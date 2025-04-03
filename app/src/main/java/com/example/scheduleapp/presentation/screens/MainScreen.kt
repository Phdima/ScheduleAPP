@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scheduleapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.presentation.viewModels.ScheduleVM


@Composable
fun MainScreen() {
    val viewModel: ScheduleVM = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Главный экран") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addEvent()
            }) {
                Icon(Icons.Default.Add, "Добавить")
            }
        },

        ) { padding ->
        ScheduleCard(
            state = state,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            onTimeChange = viewModel::updateStartTime,
            modifier = Modifier.padding(padding)
        )

    }
}
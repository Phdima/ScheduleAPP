package com.example.scheduleapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.mapping.format
import com.example.scheduleapp.presentation.state.CreateEventState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId



@Composable
fun ScheduleCardCreateView(
    state: CreateEventState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTimeChange: (LocalDateTime) -> Unit,
    onAddEvent: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showDateTimeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("Event title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween)
        {
            Button(
                onClick = { showDateTimeDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Text(state.startTime.format())
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.DateRange, null)
            }

            Button(onClick = {
                onAddEvent()
                onDismiss()
            }) {
                Text("Add Event")
            }
        }

        if (showDateTimeDialog) {
            DateTimePickerDialog(
                initialDateTime = state.startTime,
                onDismiss = { showDateTimeDialog = false },
                onConfirm = { newDateTime ->
                    onTimeChange(newDateTime)
                    showDateTimeDialog = false
                }
            )
        }

    }
}


@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(true) }


    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateTime
            .toJavaLocalDateTime()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    val timeState = rememberTimePickerState(
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Next")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TimePicker(state = timeState)
                TextButton(
                    onClick = {
                        val dateMillis = dateState.selectedDateMillis ?: return@TextButton
                        val instant = Instant.fromEpochMilliseconds(dateMillis)
                        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

                        val newDateTime = date.atTime(
                            hour = timeState.hour,
                            minute = timeState.minute
                        )
                        onConfirm(newDateTime)
                    }
                ) {
                    Text("OK")
                }

            }, dismissButton = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Back")
                }
            }
        )
    }
}









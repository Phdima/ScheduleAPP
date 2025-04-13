package com.example.scheduleapp.presentation.screens

import android.annotation.SuppressLint
import android.content.ClipData.Item
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scheduleapp.data.mapping.format
import com.example.scheduleapp.data.room.model.ScheduleEventEntity
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.presentation.viewModels.ScheduleVM
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.time.Year
import kotlin.time.Duration.Companion.days


@SuppressLint("NewApi")
@Composable
fun CalendarView() {


    val viewModel: ScheduleVM = hiltViewModel()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = emptyList())

    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val firstDayOfMonth = LocalDate(
        year = currentDate.year,
        month = currentDate.month,
        dayOfMonth = 1
    )
    val daysInMonth = when (currentDate.month) {
        Month.FEBRUARY -> 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
    val firstDayOfWeekNumber = firstDayOfMonth.dayOfWeek.isoDayNumber

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }


    fun getEventsForDate(date: LocalDate): List<ScheduleEvent> {
        return events.filter { event ->
            event.startTime.toLocalDate() == date
        }
    }

    LazyColumn(
        modifier = Modifier.heightIn(max = 600.dp)
    ) {

        item() {
            Box(
                modifier = Modifier
                    .height(350.dp)
                    .padding(8.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(15.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)

            )
            {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.padding(8.dp)
                ) {


                    item(span = { GridItemSpan(7) }) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            DayOfWeek.values().forEach { day ->
                                Text(
                                    text = day.name,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items((firstDayOfWeekNumber - 1) % 7) {
                        Spacer(modifier = Modifier.size(50.dp))
                    }

                    items(daysInMonth) { dayIndex ->
                        val date = firstDayOfMonth.plus(dayIndex, DateTimeUnit.DAY)
                        val dailyEvents = getEventsForDate(date)

                        DayCell(
                            day = date.dayOfMonth.toString(),
                            isToday = date == currentDate,
                            events = dailyEvents,
                            onClick = { selectedDate = date }
                        )

                    }
                }
            }
        }
        item() {
            selectedDate?.let { date ->
                val dailyEvents = getEventsForDate(date)
                if (dailyEvents.isNotEmpty()) {
                    DailyEventsCard(
                        events = dailyEvents,
                        onDismiss = { selectedDate = null }
                    )
                }
            }
        }
    }
}

@Composable
fun DailyEventsCard(events: List<ScheduleEvent>, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .heightIn(max = 1000.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .clickable(onClick = onDismiss),
        ) {
            events.forEach { event ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {
                    Column() {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (event.description.isNotBlank()) {
                            Text(
                                text = event.description,
                                maxLines = 3,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            Text(
                                text = "Ты Великолепен!",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.align(TopEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = event.startTime.format(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: String,
    isToday: Boolean,
    events: List<ScheduleEvent>,
    maxEventsToShow: Int = 3,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(1.dp)
            .background(
                color = if (isToday) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
            )
            .clickable {
                onClick()
            },
        contentAlignment = TopEnd
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            events.take(maxEventsToShow).forEach { event ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(color = Color(event.color))
                )
            }

        }
        Text(
            text = day,
            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onBackground
        )
    }
}


fun Instant.toLocalDate(): LocalDate {
    return this.toLocalDateTime(TimeZone.currentSystemDefault()).date
}
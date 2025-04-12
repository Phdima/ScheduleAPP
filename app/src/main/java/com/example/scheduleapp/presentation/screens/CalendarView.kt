package com.example.scheduleapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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


    ) {

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

                val hasEvent = events.any { event ->
                    event.startTime.toLocalDate() == date
                }

                DayCell(
                    day = date.dayOfMonth.toString(),
                    isToday = date == currentDate,
                    hasEvent = hasEvent
                )
            }

        }
    }
}

@Composable
fun DayCell(
    day: String,
    isToday: Boolean,
    hasEvent: Boolean
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(1.dp)
            .background(
                color = if (isToday) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        contentAlignment = TopEnd
    ) {
        if (hasEvent) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
            )
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
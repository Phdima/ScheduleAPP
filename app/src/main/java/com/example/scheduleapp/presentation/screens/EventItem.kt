package com.example.scheduleapp.presentation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.room.Delete
import com.example.scheduleapp.data.mapping.format
import com.example.scheduleapp.domain.model.ScheduleEvent

@Composable
fun EventItem(
    event: ScheduleEvent,
    modifier: Modifier = Modifier,
    onClick: (ScheduleEvent) -> Unit = {},
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(200.dp)
            .height(200.dp)
            .clickable { onClick(event) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDelete() }, modifier = Modifier.size(25.dp)) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    maxLines = 5,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .notebookLines(),
                )
            } else {
                Text(
                    text = "Ты Великолепен!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .notebookLines(),
                )
            }


            Row(verticalAlignment = Alignment.CenterVertically) {
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

fun Modifier.notebookLines(
    lineSpacing: Dp = 24.dp,
    lineColor: Color = Color.LightGray.copy(alpha = 0.9f),
    strokeWidth: Dp = 1.dp
) = this.drawWithContent {

    val lineHeightPx = lineSpacing.toPx()
    var currentY = 0f

    while (currentY <= size.height) {
        drawLine(
            color = lineColor,
            start = Offset(0f, currentY),
            end = Offset(size.width, currentY),
            strokeWidth = strokeWidth.toPx()
        )
        currentY += lineHeightPx
    }


    drawContent()
}

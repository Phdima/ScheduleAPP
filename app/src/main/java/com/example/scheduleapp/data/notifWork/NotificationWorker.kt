package com.example.scheduleapp.data.notifWork

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.R
import com.example.scheduleapp.domain.model.ScheduleEvent
import com.example.scheduleapp.domain.repository.ScheduleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val repository: ScheduleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getLong("event_id", -1)
        Log.d("Notification", "Event ID from input: $eventId")



        val now = Clock.System.now()
        val events = repository.getEventsForNotification(now..now.plus(1.hours))

        events.forEach { event ->
            showNotification(event)
            Log.d("Notification", "Processing event: ${event.id}, notificationTime = ${event.startTime - event.notificationOffset}")
        }

        return Result.success()

    }



    private fun showNotification(event: ScheduleEvent) {

        Log.d("Notification", "Attempting to show notification for event: ${event.title}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Notification", "Permission denied!")
            return
        }

        Log.d("Notification", "Permission granted or not required (API < 33)")

        val notification = NotificationCompat.Builder(context, "schedule_channel")
            .setContentTitle(event.title)
            .setContentText(event.description)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = System.currentTimeMillis().toInt()
        manager.notify(notificationId, notification)
        Log.d("Notification", "Showing notification for event: ${event.title}")
    }
}